/* 일반 문장 검출하는 코드 



모듈 프로그램과 같은 느낌으로 모든 문장들을 검사할때 사용할 도구 */

package org.languagetool.rules.ko;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;

final class StandardSentence {
	public StandardSentence() {

	}

	public int FindSubjectEndIdx(AnalyzedSentence sentence) {
		int SubjectIdx = 1;
		AnalyzedTokenReadings[] tokens = sentence.getTokensWithoutWhitespace();
		for (int i = 1; i < tokens.length; i++) {
			AnalyzedTokenReadings token = tokens[i];
			for (AnalyzedToken analyzedToken : token.getReadings()) {
				if ((analyzedToken.getPOSTag().equals("JX") || analyzedToken.getPOSTag().equals("JKS")) && i != 0) {
					SubjectIdx = i;
					break;
				}
			}
		}
		return SubjectIdx;						// 1이면 주어가 없는것
	}

	public int FindSubjectStartIdx(AnalyzedSentence sentence, int SubjectEndIdx) {
		AnalyzedTokenReadings[] tokens = sentence.getTokensWithoutWhitespace();
		int SubjectStartIdx = 1;
		for (int i = SubjectEndIdx - 1; i > 0; i--) {
			AnalyzedTokenReadings token = tokens[i];
			for (AnalyzedToken analyzedToken : token.getReadings()) {
				if ((analyzedToken.getPOSTag().equals("NNP") || analyzedToken.getPOSTag().equals("NNG")
						|| analyzedToken.getPOSTag().equals("JKG")) && i != 0) {
					continue;
				} else {
					SubjectStartIdx = i + 1;
					return SubjectStartIdx;				// 주어의 시작부 인덱스를 찾아 반환

				}
			}
		}
		return SubjectStartIdx;							//주어가 없다면 1을 반환
	}

	public int FindObjectEndIdx(AnalyzedSentence sentence, int SubjectEndIdx) {
		AnalyzedTokenReadings[] tokens = sentence.getTokensWithoutWhitespace();
		int ObjectEndIdx = SubjectEndIdx;
		for (int i = SubjectEndIdx; i < tokens.length; i++) {
			AnalyzedTokenReadings token = tokens[i];
			for (AnalyzedToken analyzedToken : token.getReadings()) {
				if ((analyzedToken.getPOSTag().equals("JKO")) && i != SubjectEndIdx) {
					ObjectEndIdx = i;
					break;
				}
			}
		}
		return ObjectEndIdx;						// 목적어를 찾아 을/를의 인덱스를 반환. 못찾았다면 주어의 끝 인덱스를 반환    만약 목적어가 없다면 0 을 반환해야할지 주어의 끝 인덱스를 반환해야할지 안정함.
	}

	public int FindObjectStartIdx(AnalyzedSentence sentence, int SubjectEndIdx, int ObjectEndIdx) {
		AnalyzedTokenReadings[] tokens = sentence.getTokensWithoutWhitespace();
		int ObjectStartIdx = SubjectEndIdx + 1;
		if (ObjectEndIdx == SubjectEndIdx)						// 목적어가 없다면
			return SubjectEndIdx;					// 주어의 끝 인덱스를 반환
		for (int i = ObjectEndIdx; i > SubjectEndIdx; i--) {
			AnalyzedTokenReadings token = tokens[i];
			for (AnalyzedToken analyzedToken : token.getReadings()) {
				if ((analyzedToken.getPOSTag().equals("NNP") || analyzedToken.getPOSTag().equals("NNG")
						|| analyzedToken.getPOSTag().equals("JKG")) && i != 0) {
					continue;
				} else {
					ObjectStartIdx = i + 1;
					return ObjectStartIdx;					// 진목적어를 찾아 그 인덱스 반환

				}
			}
		}
		return ObjectStartIdx;							// 별 의미없어보임.
	}
	public int FindObjectDependentPhrase(AnalyzedSentence sentence,ArrayList<Integer> ETMlist ,int SubjectEndIdx, int ObjectStartIdx) {	//목적어를 수식하는 관형절이 있다면 
		
		AnalyzedTokenReadings[] tokens = sentence.getTokensWithoutWhitespace();
		int ObjectDependentPhraseStartIdx =0;
		if(SubjectEndIdx == ObjectStartIdx){	//목적어가 없다면
			return 0;
		}
		for (int i = SubjectEndIdx; i < ObjectStartIdx; i++) {
			AnalyzedTokenReadings token = tokens[i];
			for (AnalyzedToken analyzedToken : token.getReadings()) {
				if ((analyzedToken.getPOSTag().equals("ETM")) && i != SubjectEndIdx) {	//수식절의 index 반환
					ObjectDependentPhraseStartIdx=i;
					ETMlist.add(ObjectDependentPhraseStartIdx);
					FindObjectDependentPhrase(sentence,ETMlist,i+1,ObjectStartIdx);
				}
			}
		}
		return 0;
	}
	//public int 																		//ETM 리스트가 비어 있다면 주어 인덱스를 시작으로 부사어가 있는지 확인하고 주어와 목적어 사이에 부사어가 있다면 오류 플래그  
																					//ETM 리스트가 비어있지 않다면 목
}