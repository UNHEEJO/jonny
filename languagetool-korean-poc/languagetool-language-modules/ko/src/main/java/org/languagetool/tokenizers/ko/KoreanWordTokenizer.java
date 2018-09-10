/* LanguageTool, a natural language style checker 
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.tokenizers.ko;

import java.util.ArrayList;
import java.util.List;

import org.languagetool.tokenizers.ko.StandardSentence;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

import org.languagetool.tokenizers.Tokenizer;

public class KoreanWordTokenizer implements Tokenizer {

	Komoran komoran = new Komoran("C:\\Users\\Jony\\git\\repository\\languagetool-korean-poc\\KOMORAN\\models-full");

	public KoreanWordTokenizer() {
	}

	@Override
	public List<String> tokenize(String text) {
		List<String> ret = new ArrayList<>();
		List<String> ret2 = new ArrayList<>();
		List<Pair<String, String>> Objects = new ArrayList<>();
		List<Pair<String, String>> Result = new ArrayList<>();
		int lengths = 0;
		int j = 0;
		int cusorslength = 0;
		String[] Splitwords = text.split("\\s");

		// 띄어쓰기
		text = text.replace("하는 기능을 제공해야한다.", "한다.");
		System.out.println(text);
		komoran.setUserDic(
				"C:\\Users\\Jony\\git\\repository\\languagetool-korean-poc\\KOMORAN\\KOMORAN-master\\user_data\\dic.user");
		List<List<Pair<String, String>>> analyzeResultList = komoran.analyze(text);
		System.out.println(analyzeResultList);
		for (List<Pair<String, String>> wordResultList : analyzeResultList) {
			System.out.println(wordResultList);
			System.out.println("문장의 길이 = " + wordResultList.size());
			for (int i = 0; i < wordResultList.size(); i++) {
				Pair<String, String> pair = wordResultList.get(i);
				if (pair.getFirst().equals("하") && wordResultList.get(i + 1).getFirst().equals("ㄴ다")) {
					Pair<String, String> temp_pair = new Pair<String, String>("한다", "EF");
					Objects.add(temp_pair);
					i++;
				} else if (pair.getFirst().equals("ㄴ다")) {
					Pair<String, String> temp_pair = new Pair<String, String>("다", pair.getSecond());
					Objects.add(temp_pair);
				} else if (pair.getFirst().equals("따르") && wordResultList.get(i + 1).getFirst().equals("ㄴ")) {
					Pair<String, String> temp_pair = new Pair<String, String>("따른", "ETM");
					Objects.add(temp_pair);
					i++;
				} else if (pair.getFirst().equals("의하") && wordResultList.get(i + 1).getFirst().equals("아서")) {
					Pair<String, String> temp_pair = new Pair<String, String>("의해서", "MAJ");
					Objects.add(temp_pair);
					i++;
				} else if (pair.getFirst().equals("되") && wordResultList.get(i + 1).getFirst().equals("ㄴ")) {
					Pair<String, String> temp_pair = new Pair<String, String>("된", "ETM");
					Objects.add(temp_pair);
					i++;
				} else if (pair.getFirst().equals("되") && wordResultList.get(i + 1).getFirst().equals("는")) {
					Pair<String, String> temp_pair = new Pair<String, String>("되는", "ETM");
					Objects.add(temp_pair);
					i++;
				} else if (!pair.getSecond().equals("JKS")
						&& (pair.getFirst().equals("이") && wordResultList.get(i + 1).getFirst().equals("ㄴ"))) {
					Pair<String, String> temp_pair = new Pair<String, String>("인", "ETM");
					Objects.add(temp_pair);
					i++;
				} else if (!(wordResultList.size() <= i + 1)
						&& (pair.getFirst().equals("하") && wordResultList.get(i + 1).getFirst().equals("ㄴ"))) {
					Pair<String, String> temp_pair = new Pair<String, String>("한", "ETM");
					Objects.add(temp_pair);
					i++;
				} else if (pair.getSecond().equals("XSV")
						&& (pair.getFirst().equals("하") && wordResultList.get(i + 1).getFirst().equals("ㄹ"))) {
					Pair<String, String> temp_pair = new Pair<String, String>("할", "ETM");
					Objects.add(temp_pair);
					i++;
				} else if (pair.getFirst().equals("하") && wordResultList.get(i + 1).getFirst().equals("아야")) {
					Pair<String, String> temp_pair = new Pair<String, String>("해야", "XSV");
					Objects.add(temp_pair);
					i++;
				} else
					Objects.add(pair);
				System.out.println("현 idx = " + i);
			}
		}
		System.out.println("Objects" + Objects);
		for (Pair<String, String> cusor : Objects) {
			System.out.println("cusor");
			System.out.println(cusor);
			List<List<Pair<String, String>>> splitedandPosed = komoran.analyze(Splitwords[j]);

			if (lengths == 0) { //
				for (List<Pair<String, String>> becounted : splitedandPosed) {
					for (int i = 0; i < becounted.size(); i++) {
						Pair<String, String> pair = becounted.get(i);
						// if (pair.getFirst().equals("하") && becounted.get(i +
						// 1).getFirst().equals("ㄴ다")) {
						// lengths += pair.getFirst().length() - 1;
						// } else
						if (pair.getFirst().equals("ㄴ다")) {
							lengths += pair.getFirst().length() - 1;
						} else if (pair.getFirst().equals("따르") && becounted.get(i + 1).getFirst().equals("ㄴ")) {
							lengths += pair.getFirst().length() - 1;
						} else if (pair.getFirst().equals("의하") && becounted.get(i + 1).getFirst().equals("아서")) {
							lengths += pair.getFirst().length() - 1;
						} else if (pair.getFirst().equals("되") && becounted.get(i + 1).getFirst().equals("ㄴ")) {
							lengths += pair.getFirst().length() - 1;
						} else if (pair.getFirst().equals("되") && becounted.get(i + 1).getFirst().equals("는")) {
							lengths += pair.getFirst().length();
						} else if (!pair.getSecond().equals("JKS")
								&& (pair.getFirst().equals("이") && becounted.get(i + 1).getFirst().equals("ㄴ"))) {
							lengths += pair.getFirst().length() - 1;
						} else if (!(becounted.size() <= i + 1)
								&& (pair.getFirst().equals("하") && becounted.get(i + 1).getFirst().equals("ㄴ"))) {
							lengths += pair.getFirst().length() - 1;
						} else if (pair.getSecond().equals("XSV")
								&& (pair.getFirst().equals("하") && becounted.get(i + 1).getFirst().equals("ㄹ"))) {
							lengths += pair.getFirst().length() - 1;
						} else if ((pair.getFirst().equals("하") && becounted.get(i + 1).getFirst().equals("아야"))) {
							lengths += pair.getFirst().length() - 1;
						} else
							lengths += pair.getFirst().length();
					}
				}
			}
			cusorslength += cusor.getFirst().length();
			System.out.println("lengths   = " + lengths + "   cusorslength   = " + cusorslength);
			if (cusorslength < lengths) {
				Pair<String, String> temp_pair2 = new Pair<String, String>(cusor.getFirst(), cusor.getSecond());
				Result.add(temp_pair2);
				// ret.add(cusor.getFirst() + "&" + cusor.getSecond() + "&" + cusor.getFirst());
			} else if (cusorslength == lengths) {
				Pair<String, String> temp_pair2 = new Pair<String, String>(cusor.getFirst(), cusor.getSecond());
				Result.add(temp_pair2);
				// ret.add(cusor.getFirst() + "&" + cusor.getSecond() + "&" + cusor.getFirst());

				if (!(cusor.getFirst().equals("."))) {
					System.out.println("The processing when space pair is inserted");
					Pair<String, String> temp_pair3 = new Pair<String, String>("\b", "space");
					Result.add(temp_pair3);
					// ret.add("\b" + "&" + "space" + "&" + "\b");
				}
				j++;
				lengths = 0;
				cusorslength = 0;
			}

		}
		// System.out.println("이거는 초기버전이에오");
		// StandardSentence Prgrph = new StandardSentence(Result);
		//
		// if (!Prgrph.Subject.isEmpty()) {
		// ret.add(Prgrph.Subject + "&" + "Subject" + "&" + Prgrph.Subject);
		// ret.add("\b" + "&" + "Space" + "&" + "\b");
		// }
		// if (!Prgrph.Object.isEmpty()) {
		// ret.add(Prgrph.Object + "&" + "Object" + "&" + Prgrph.Object);
		// ret.add("\b" + "&" + "Space" + "&" + "\b");
		// }
		// if (!Prgrph.Complement.isEmpty()) {
		// ret.add(Prgrph.Complement + "&" + "Complemnet" + "&" + Prgrph.Complement);
		// ret.add("\b" + "&" + "Space" + "&" + "\b");
		// }
		// if (!Prgrph.Adverb.isEmpty()) {
		// ret.add(Prgrph.Adverb.get(0).getFirst() + "&" + "Adverb" + "&" +
		// Prgrph.Adverb.get(0).getFirst());
		// ret.add("\b" + "&" + "Space" + "&" + "\b");
		// }
		// if (!Prgrph.Verb.isEmpty()) {
		// ret.add(Prgrph.Verb + "&" + "Verb" + "&" + Prgrph.Verb);
		// }
		// System.out.println(ret);

		BasicSentence<Pair<String, String>> Uni = new BasicSentence(Result);
		List<Pair<String,String>> Unhee = new ArrayList<>();
		for(Pair<String,String> Temp : Uni.tokens) {
			if(Temp.getSecond().equals("Verb")) {
				Unhee.add(new Pair<String,String>(Temp.getFirst().replace("한다", "하는 기능을 제공해야한다"),Temp.getSecond()));
			}
			else {
				Unhee.add(Temp);
			}
		}
		for (Pair<String, String> KTokens : Unhee) {
			ret2.add(KTokens.getFirst() + "&" + KTokens.getSecond() + "&" + KTokens.getFirst());
			// Cnt ++;
			// if(Cnt < Uni.tokens.size()) ret2.add("\b" + "&" + "Space" + "&" + "\b");
		}
		System.out.println(ret2);
		return ret2;

	}

}
