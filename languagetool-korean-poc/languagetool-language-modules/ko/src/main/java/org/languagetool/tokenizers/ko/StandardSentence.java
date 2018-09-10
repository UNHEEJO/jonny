/* 일반 문장 검출하는 코드 



모듈 프로그램과 같은 느낌으로 모든 문장들을 검사할때 사용할 도구 */

package org.languagetool.tokenizers.ko;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;

import kr.co.shineware.util.common.model.Pair;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class StandardSentence {
	String Subject = new String();
	String Object = new String();
	String ErrString = new String();
	List<Pair<String, Integer>> Adverb = new ArrayList<>();

	// String Adverb = new String();
	String Verb = new String();
	String Complement = new String();
	int State = 0; // 0. Err 1.주어만 있음 2. 주어 목적어 있음 3. 주어 보어 있음 4.주부보술

	int SubjectEndIdx = 0;
	int SubjectStartIdx = 0;

	int ObjectEndIdx = 0;
	int ObjectStartIdx = 0;

	int ComplementStartIdx = 0;
	int ComplementEndIdx = 0;

	int AdverbEndIdx = 0;
	int AdverbStartIdx = 0;

	int VerbEndIdx = 0;
	int VerbStartIdx = 0;

	int TempAdverbIdx = 0;
	public StandardSentence(List<Pair<String, String>> Result) {
		

		this.SubjectEndIdx = FindSubjectEndIdx(Result);
		System.out.println("객체에 저장되는 값" + this.SubjectEndIdx);
		if (SubjectEndIdx == -1) {
			this.State = 0;
		} else {
			this.Subject = Merger(Result, 0, this.SubjectEndIdx);
			this.State = 1;

			this.ObjectEndIdx = FindObjectEndIdx(Result, this.SubjectEndIdx + 2);
			if (this.ObjectEndIdx != -1) { // 목적어가 있을경우

				this.ObjectStartIdx = this.SubjectEndIdx + 2;
				this.Object = Merger(Result, this.ObjectStartIdx, this.ObjectEndIdx);
				this.State = 2;
			} else { // 목적어가 없을경우 보어 판단
				this.ComplementEndIdx = FindComplementEndIdx(Result, this.SubjectEndIdx + 2);
				if (this.ComplementEndIdx != 0) {
					this.ComplementStartIdx = this.SubjectEndIdx + 2;
					this.Complement = Merger(Result, this.ComplementStartIdx, this.ComplementEndIdx);
					this.State = 3;
				}
				else this.State=1;

			}

			if (this.State == 1) { // 주어만 있을경우
				this.AdverbEndIdx = FindAdverbEndIdx(Result, this.SubjectEndIdx);
				if (this.AdverbEndIdx != 0) { // 부사어가 있을경우
					this.AdverbStartIdx = this.SubjectEndIdx + 2;
					Pair<String, Integer> temp = new Pair(Merger(Result, this.AdverbStartIdx, this.AdverbEndIdx), this.AdverbEndIdx);
					this.Adverb.add(temp);
					this.VerbEndIdx = FindVerbEndIdx(Result);
					if (this.VerbEndIdx != 0)
						this.Verb = Merger(Result, this.AdverbEndIdx + 2, this.VerbEndIdx);
					else
						this.State = 0;
				} else { // 부사어가 없을경우
					this.VerbEndIdx = FindVerbEndIdx(Result);
					if (this.VerbEndIdx != 0)
						this.Verb = Merger(Result, this.SubjectEndIdx + 2, this.VerbEndIdx);
					else
						this.State = 0;
				}
			} else if (this.State == 2) { // 주어 목적어가 있을 경우
				System.out.println("주어 인덱스 "+this.SubjectEndIdx +"목적어 인덱스 " +this.ObjectEndIdx);
				if (AdverbDetector(Result, this.SubjectEndIdx, this.ObjectEndIdx) == 0) {
					System.out.println("설마??");
					int temp_adverb = FindAdverbEndIdx(Result, this.SubjectEndIdx, this.ObjectEndIdx);
					Pair<String, Integer> temp = new Pair(Merger(Result, this.SubjectEndIdx + 2, temp_adverb), temp_adverb);
					this.Adverb.add(temp);

				}
				this.AdverbEndIdx = FindAdverbEndIdx(Result, this.ObjectEndIdx);
				System.out.println("부사어 인덱스 = " + this.AdverbEndIdx);
				if (this.AdverbEndIdx != 0) { // 부사어가 있을경우
					this.AdverbStartIdx = this.ObjectEndIdx + 2;
					System.out.println("단계좀 보자 " + this.AdverbStartIdx);
					System.out.println(Merger(Result, this.AdverbStartIdx, this.AdverbEndIdx));
					
					Pair<String, Integer> temp = new Pair(Merger(Result, this.AdverbStartIdx, this.AdverbEndIdx), this.AdverbEndIdx);
					System.out.println("부사어 잘 합쳐졌니 ? = 네! = " +temp.getFirst() +"인덱스는 잘 뱉었니 ? =" +temp.getSecond());
					this.Adverb.add(temp);
					System.out.println("부사어 = " + this.Adverb.get(0).getFirst() + "부사어 인덱스 = "+ this.Adverb.get(0).getSecond());
					this.VerbEndIdx = FindVerbEndIdx(Result);
					if (this.VerbEndIdx != 0)
						this.Verb = Merger(Result, this.AdverbEndIdx + 2, this.VerbEndIdx);
					else
						this.State = 0;
				} else { // 부사어가 없을경우
					this.VerbEndIdx = FindVerbEndIdx(Result);
					if (this.VerbEndIdx != 0)
						this.Verb = Merger(Result, this.ObjectEndIdx + 2, this.VerbEndIdx);
					else
						this.State = 0;
				}

			} else if (this.State == 3) {				//보어
				if (AdverbDetector(Result, this.SubjectEndIdx, this.ObjectEndIdx) == 0) {
					int temp_adverb = FindAdverbEndIdx(Result, this.SubjectEndIdx, this.ObjectEndIdx);
					Pair<String, Integer> temp = new Pair(Merger(Result, this.SubjectEndIdx + 2, temp_adverb), temp_adverb);
					this.Adverb.add(temp);
					this.State = 4;
				}
				this.VerbEndIdx = FindVerbEndIdx(Result);
				if (this.VerbEndIdx != 0)
					this.Verb = Merger(Result, this.ComplementEndIdx + 2, this.VerbEndIdx);
				else
					this.State = 0;
			}
			if (this.State == 0) {
				this.ErrString = Merger(Result, 0, Result.size());
			}
		}
	}

	public int FindSubjectEndIdx(List<Pair<String, String>> Result) {						
		int SubjectEndIdx = 0;
		int i = SubjectEndIdx;
		System.out.println("여길로 안들어갑니까?");
		for (Pair<String, String> temp : Result) {
			System.out.println(temp.getSecond());
			if (temp.getSecond().equals("JX") || temp.getSecond().equals("JKS")) {
				SubjectEndIdx = i;
				System.out.println(SubjectEndIdx);
				return SubjectEndIdx;
			}
			i++;
		}
		if (SubjectEndIdx == 0) {
			SubjectEndIdx = -1;
		}
		return SubjectEndIdx; // -1이면 주어가 없는것
	}

	public int FindSubjectStartIdx(List<Pair<String, String>> Result, int SubjectEndIdx) {
		int SubjectStartIdx = 1;
		for (int i = SubjectEndIdx - 1; i > 0; i--) {
			if (Result.get(i).getSecond().equals("NNP") || Result.get(i).getSecond().equals("NNG")
					|| Result.get(i).getSecond().equals("JKG") && i != 0) {
				SubjectStartIdx = i;
				continue;
			} else {
				SubjectStartIdx = i + 1;
				return SubjectStartIdx; // 주어의 시작부 인덱스를 찾아 반환

			}
		}
		return SubjectStartIdx;
	}

	public int FindComplementEndIdx(List<Pair<String, String>> Result, int SubjectEndIdx) {
		int ComplementIdx = 0;
		for (int i = SubjectEndIdx + 1; i < Result.size(); i++) {
			if (Result.get(i).getSecond().equals("JKS") || Result.get(i).getSecond().equals("JX")) {
				ComplementIdx = i;
				return ComplementIdx;
			}

		}
		return 0;
	}

	public int FindObjectEndIdx(List<Pair<String, String>> Result, int SubjectEndIdx) {
		int ObjectEndIdx = SubjectEndIdx;
		for (int i = SubjectEndIdx; i < Result.size(); i++) {
			if (Result.get(i).getSecond().equals("JKO") && i != SubjectEndIdx) {
				ObjectEndIdx = i;
				break;
			}
		}
		if (SubjectEndIdx == ObjectEndIdx)
			return -1; // 목적어가 없다면 -1을 반환
		return ObjectEndIdx; // 목적어를 찾아 을/를의 인덱스를 반환. 못찾았다면 주어의 끝 인덱스를 반환 만약 목적어가 없다면 0 을 반환해야할지 주어의 끝 인덱스를
								// 반환해야할지 안정함.
	}

	public int FindObjectStartIdx(List<Pair<String, String>> Result, int SubjectEndIdx, int ObjectEndIdx) {
		int ObjectStartIdx = SubjectEndIdx + 1;
		for (int i = ObjectEndIdx; i > SubjectEndIdx; i--) {
			if ((Result.get(i).getSecond().equals("NNP") || Result.get(i).getSecond().equals("NNG")
					|| Result.get(i).getSecond().equals("JKG")) && i != 0) {
				continue;
			} else {
				ObjectStartIdx = i + 1;
				return ObjectStartIdx; // 진목적어를 찾아 그 인덱스 반환

			}
		}
		return ObjectStartIdx; // 별 의미없음
	}

	public int FindVerbEndIdx(List<Pair<String, String>> Result) {
		for (int i = Result.size() - 1; i > 0; i--) {
			if (Result.get(i).getSecond().equals("SF"))
				return i;
		}
		return 0;
	}

	public int FindAdverbEndIdx(List<Pair<String, String>> Result, int StartIdx) {
		int AdverbEndIdx = 0;
		for (int i = StartIdx; i < Result.size(); i++) {
			if (Result.get(i).getSecond().equals("JKB") || Result.get(i).getSecond().equals("MAJ")) {
				AdverbEndIdx = i;
			}
		}
		return AdverbEndIdx;
	}

	public int FindAdverbEndIdx(List<Pair<String, String>> Result, int StartIdx, int EndIdx) {
		int AdverbEndIdx = 0;
		for (int i = StartIdx; i <= EndIdx; i++) {
			if (Result.get(i).getSecond().equals("JKB") || Result.get(i).getSecond().equals("MAJ")) {
				AdverbEndIdx = i;
			}
		}
		return AdverbEndIdx;
	}

	public int AdverbDetector(List<Pair<String, String>> Result, int SubjectEndIdx, int ObjectEndIdx) { 
		int ObjectAdverbIdx = 0;
		int AdverbIdx = 0;
		int ETMidx = 0;
		for (int i = SubjectEndIdx; i < ObjectEndIdx; i++) {
			if (Result.get(i).getSecond().equals("JKB") || Result.get(i).getSecond().equals("MAJ")) {				//수정해야함
				if (ObjectAdverbIdx == 0) {
					ETMidx = ETMFinder(Result, SubjectEndIdx, i);
					if (ETMidx > i && ETMidx != 0)
						ObjectAdverbIdx = ETMidx;
					else
						AdverbIdx = i;

				} else {
					ETMidx = ETMFinder(Result, ObjectAdverbIdx, i);
					if (ETMidx > i && ETMidx != 0)
						ObjectAdverbIdx = i;
					else
						AdverbIdx = i;
				}

			}
			else AdverbIdx = -1;
		}
		return AdverbIdx;
	}

	public int ETMFinder(List<Pair<String, String>> Result, int StartIdx, int EndIdx) {

		for (int i = StartIdx; i >= EndIdx; i++) {
			if (Result.get(i).getSecond().equals("ETM")) {
				return i;
			}

		}
		return 0;
	}

	public String Merger(List<Pair<String, String>> Result, int StartIdx, int EndIdx) {
		StringBuffer temp_Phrase = new StringBuffer();
		for (int i = StartIdx; i <= EndIdx; i++) {
			temp_Phrase.append(Result.get(i).getFirst());
		}
		String Result_Phrase = temp_Phrase.toString();
		return Result_Phrase;
	}
	

}
