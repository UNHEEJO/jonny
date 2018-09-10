package org.languagetool.tokenizers.ko;

import org.languagetool.tokenizers.ko.Deque;
import org.languagetool.tokenizers.ko.Stack;
import org.languagetool.tokenizers.ko.TreeNode;
import kr.co.shineware.util.common.model.Pair;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class BasicSentence<T> {

	List<Pair<String, String>> tokens = new ArrayList<>();
	List<Pair<String, String>> tokensWithoutSpace = new ArrayList<>();
	TreeNode<T> SyntaxTree = InitializeTree();

	public TreeNode<T> InitializeTree() {
		Pair<String, String> Temp = new Pair("Syntax", "tree");
		return new TreeNode<T>(Temp);
	}

	public BasicSentence(List<Pair<String, String>> Result) {
		this.tokens = ETM_Phrase_Combine3(SetComponents(AnalyzeKTokens(DivideSentence2(CombineNouns2(Result)))));
		// this.tokens =
		// ETM_Phrase_Combine(SetComponents(AnalyzeKTokens(DivideSentence(CombineNouns2(tokensWithoutSpace)))));

		// for (Pair<String, String> token : this.tokens) {
		// System.out.println("컴바인한 결과 GET FIRST = " + token.getFirst() + " GET SECOND "
		// + token.getSecond());
		// }
		for (Pair<String, String> token : this.tokens) {
			if (token.getSecond().equals("space"))
				continue;
			else
				this.tokensWithoutSpace.add(token);
		}

		this.tokens = AddSpace(AdverbWraaping(WrappingETM(this.tokens)));
		MaketheSynTaxTree(this.tokensWithoutSpace);

	}

	public List<Pair<String, String>> AddSpace(List<Pair<String, String>> Result) {
		List<Pair<String, String>> ReturnResult = new ArrayList<>();
		Pair<String, String> spacePair = new Pair("\b", "space");
		for (int i = 0; i < Result.size(); i++) {
			ReturnResult.add(Result.get(i));
			if (i != Result.size() - 1) {
				ReturnResult.add(spacePair);
			}
		}
		return ReturnResult;
	}

	public List<Pair<String, String>> WrappingETM(List<Pair<String, String>> Result) {
		List<Pair<String, String>> ReturnResult = new ArrayList<>();
		StringBuffer Temp_String = new StringBuffer();
		Deque ETMqueue = new Deque();
		System.out.println("======================관형절 래핑단계에 들어오는 인풋======================");
		for (Pair<String, String> Temp : Result) {
			System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
		}
		for (Pair<String, String> Temp : Result) {
			if (Temp.getSecond().equals("ETM_Phrase") || Temp.getSecond().equals("Passive_ETM_Phrase")) {
				ETMqueue.insertRear(Temp);
			} else {
				if (!ETMqueue.isEmpty()) {
					while (!ETMqueue.isEmpty()) {
						Temp_String.append(ETMqueue.deleteFront().getFirst());
						Temp_String.append("\b");
					}
					ReturnResult.add(new Pair(Temp_String.toString() + Temp.getFirst(), Temp.getSecond()));

					Temp_String.delete(0, Temp_String.length());
				} else {

					ReturnResult.add(Temp);
				}
			}

		}
		System.out.println("======================관형절 래핑하는 부분 아웃풋 출력======================");
		for (Pair<String, String> Temp : ReturnResult) {
			System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
		}
		return ReturnResult;
	}

	public TreeNode<T> MaketheSynTaxTree(List<Pair<String, String>> tokens) { // space없다고 생각하고 하자.
		Stack ETMStack = new Stack();
		System.out.println("[ Syntax Tree ]");
		for (Pair<String, String> Token : tokens) {
			if (Token.getSecond().equals("ETM_Phrase")) {
				ETMStack.push(Token);
			} else {
				TreeNode<T> Temp_Node = new TreeNode(new Pair(Token.getSecond(), Token.getSecond()));
				System.out.println("[" + Token.getSecond() + " / " + Token.getSecond() + "]");
				while (!ETMStack.isEmpty()) {
					System.out.print("[" + ETMStack.peek().getFirst() + " / " + ETMStack.peek().getSecond() + "]");
					System.out.print("          ");
					Temp_Node.addChild(ETMStack.pop());
				}
				System.out.println("[" + Token.getFirst() + " / " + Token.getSecond() + "]");
				Temp_Node.addChild(new Pair(Token.getFirst(), Token.getSecond()));
				this.SyntaxTree.addChild(Temp_Node);
			}

		}

		return null;
	}

	public List<Pair<String, String>> AdverbWraaping(List<Pair<String, String>> Result) {
		List<Pair<String, String>> ReturnResult = new ArrayList<>();
		StringBuffer Temp_String = new StringBuffer();
		Deque Adverbqueue = new Deque();
		System.out.println("======================부사어 래핑하는 부분 인풋 출력======================");
		for (Pair<String, String> Temp : Result) {
			System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
		}
		for (Pair<String, String> Temp : Result) {
			if (Temp.getSecond().equals("Adverb")) {
				Adverbqueue.insertRear(Temp);
			} else {
				if (!Adverbqueue.isEmpty()) {
					while (!Adverbqueue.isEmpty()) { // space 처리해야해.... 마지막 부사어에 스페이스바 들어감.
						Temp_String.append(Adverbqueue.deleteFront().getFirst());
						if (!Adverbqueue.isEmpty())
							Temp_String.append("\b");
					}
					ReturnResult.add(new Pair(Temp_String.toString(), "Adverb"));
					ReturnResult.add(Temp);
					Temp_String.delete(0, Temp_String.length());
				} else {

					ReturnResult.add(Temp);
				}
			}

		}
		System.out.println("======================부사어 래핑하는 부분 아웃풋 출력======================");
		for (Pair<String, String> Temp : ReturnResult) {
			System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
		}
		return ReturnResult;

	}

	public List<Pair<String, String>> ETM_Phrase_Combine3(List<Pair<String, String>> Result) {
		List<Pair<String, String>> ReturnResult = new ArrayList<>();
		StringBuffer Temp_String = new StringBuffer();
		StringBuffer Temp_Morp = new StringBuffer();
		Stack ETMStack = new Stack();
		Stack PassiveStack = new Stack();
		Deque TempDeque = new Deque();
		System.out.println("======================관형절 처리 단계======================  ");
		System.out.println("======================관형절 처리하는 부분 인풋 출력======================");
		for (Pair<String, String> Temp : Result) {
			System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
		}
		for (Pair<String, String> token : Result) {
			if (token.getSecond().equals("ETM_Phrase")) {
				if (!TempDeque.isEmpty()) {
					First: while (!TempDeque.isEmpty()) {
						if (TempDeque.peekRear().getSecond().equals("Subject")) {
							ETMStack.push(TempDeque.deleteRear());
							break First;
						} else if (TempDeque.peekRear().getSecond().equals("ETM_Phrase")
								|| TempDeque.peekRear().getSecond().equals("Passive_ETM_Phrase")||TempDeque.peekRear().getSecond().equals("ETM_Phrase_Not_Valid")||TempDeque.peekRear().getSecond().equals("Passive_ETM_Phrase_Not_Valid"))  {
							break First;
						} else
							ETMStack.push(TempDeque.deleteRear());

					}
					if (!ETMStack.isEmpty()) {
						//if (!ETMStack.peek().getSecond().equals("Subject")) Temp_Morp.append("_Not_Valid");
						while (!ETMStack.isEmpty()) {
							Temp_String.append(ETMStack.pop().getFirst() + "\b");
						}
						TempDeque.insertRear(new Pair<String, String>(Temp_String.toString() + token.getFirst(),
								token.getSecond() + Temp_Morp.toString()));
						Temp_String.setLength(0);
						Temp_Morp.setLength(0);
					} else {
						TempDeque.insertRear(
								new Pair<String, String>(token.getFirst(), token.getSecond() + "_Not_Valid"));
						Temp_String.setLength(0);
						Temp_Morp.setLength(0);
					}
				} else {
					TempDeque.insertRear(new Pair<String, String>(token.getFirst(), token.getSecond() + "_Not_Valid"));
				}
			} else if (token.getSecond().equals("Passive_ETM_Phrase")) {
				if (!TempDeque.isEmpty()) {
					Second: while (!TempDeque.isEmpty()) {
						if (TempDeque.peekRear().getSecond().equals("Adverb")) {
							PassiveStack.push(TempDeque.deleteRear());
						} else if (TempDeque.peekRear().getSecond().equals("ETM_Phrase")
								|| TempDeque.peekRear().getSecond().equals("Passive_ETM_Phrase")||TempDeque.peekRear().getSecond().equals("ETM_Phrase_Not_Valid")||TempDeque.peekRear().getSecond().equals("Passive_ETM_Phrase_Not_Valid"))  {
							break Second;
						} 
						else break Second;
					}
					if (!PassiveStack.isEmpty()) {
						if (!PassiveStack.peek().getSecond().equals("Adverb"))
							Temp_Morp.append("_Not_Valid");
						while (!PassiveStack.isEmpty()) {
							Temp_String.append(PassiveStack.pop().getFirst() + "\b");
						}
						TempDeque.insertRear(new Pair<String, String>(Temp_String.toString() + token.getFirst(),
								token.getSecond() + Temp_Morp.toString()));
						Temp_String.setLength(0);
						Temp_Morp.setLength(0);
					} 
					else {
						System.out.println("근데 스택 비지 않았나??? 왜 열로 안옴? 뒤질래?");
						TempDeque.insertRear(
								new Pair<String, String>(token.getFirst(), token.getSecond() + "_Not_Valid"));
						Temp_String.setLength(0);
						Temp_Morp.setLength(0);
					}
				} 
				else {
					TempDeque.insertRear(new Pair<String, String>(token.getFirst(), token.getSecond() + "_Not_Valid"));
				}
			} 
			else {
				TempDeque.insertRear(token);
			}
		}
		while (!TempDeque.isEmpty()) {
			ReturnResult.add(TempDeque.deleteFront());
		}
		System.out.println("======================관형절 처리하는 부분 아웃풋 출력======================");
		for (Pair<String, String> Temp : ReturnResult) {
			System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
		}
		return ReturnResult;

	}

	public List<Pair<String, String>> ETM_Phrase_Combine2(List<Pair<String, String>> Result) { // 조사를 기준으로 관형절 확립. + 다시
																								// 구현.. 관형술어가 제일 먼저 들어오는
																								// 경우가 안걸러짐.
		List<Pair<String, String>> ReturnResult = new ArrayList<>();
		StringBuffer Temp_String = new StringBuffer();
		Stack TempStack = new Stack();
		Stack PassiveStack = new Stack();
		Deque TempDeque = new Deque();
		Deque TempETMDeque = new Deque();
		System.out.println("======================관형절 처리 단계======================  ");
		System.out.println("======================관형절 처리하는 부분 인풋 출력======================");
		for (Pair<String, String> Temp : Result) {
			System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
		}
		for (Pair<String, String> token : Result) {
			if (token.getSecond().equals("ETM_Phrase")) {

				First: while (!TempDeque.isEmpty()) {
					if (TempDeque.peekRear().getSecond().equals("Subject")) {
						TempStack.push(TempDeque.deleteRear());
						while (!TempStack.isEmpty()) {
							Temp_String.append(TempStack.pop().getFirst() + "\b");
						}
						TempDeque.insertRear(new Pair(Temp_String.toString() + token.getFirst(), "ETM_Phrase"));
						Temp_String.setLength(0);
						break First;
					} else {
						TempStack.push(TempDeque.deleteRear());
					}
				}
				if (!TempStack.isEmpty()) {
					while (!TempStack.isEmpty()) {
						Temp_String.append(TempStack.pop().getFirst() + "\b");
					}
					TempDeque.insertRear(new Pair(Temp_String.toString() + token.getFirst(), "ETM_Phrase"));
					Temp_String.setLength(0);
				}
			} else if (token.getSecond().equals("Passive_ETM_Phrase")) {
				Second: while (!TempDeque.isEmpty()) {
					if (!TempDeque.peekRear().getSecond().equals("Adverb")) {
						if (!PassiveStack.isEmpty()) {
							while (!PassiveStack.isEmpty()) {
								Temp_String.append(PassiveStack.pop().getFirst() + "\b");
							}
							TempDeque.insertRear(
									new Pair(Temp_String.toString() + token.getFirst(), "Passive_ETM_Phrase"));
							Temp_String.setLength(0);
							break Second;
						} else {
							TempDeque.insertRear(new Pair(token.getFirst(), token.getSecond() + "_not_Valid"));
							break Second;
						}
					} else {
						PassiveStack.push(TempDeque.deleteRear());
					}
				}
			} else {
				TempDeque.insertRear(token);
			}
		}
		while (!TempDeque.isEmpty()) {
			ReturnResult.add(TempDeque.deleteFront());
		}
		System.out.println("======================관형절 처리하는 부분 아웃풋 출력======================");
		for (Pair<String, String> Temp : ReturnResult) {
			System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
		}
		return ReturnResult;
	}

	public List<Pair<String, String>> ETM_Phrase_Combine(List<Pair<String, String>> Result) {
		List<Pair<String, String>> ReturnResult = new ArrayList<>();
		StringBuffer Temp_String = new StringBuffer();
		Stack TempStack = new Stack();
		Deque TempDeque = new Deque();
		for (Pair<String, String> Temp : Result) {

			System.out.println("ETM 합칠때 들어오는 입력값들 = " + Temp.getFirst() + "/" + Temp.getSecond());
		}
		Pair<String, String> SpacePair = new Pair("\b", "space");
		for (Pair<String, String> temp : Result) {
			if (temp.getSecond().equals("ETM_Phrase")) {
				TempDeque.insertRear(temp);
				TempDeque.insertRear(SpacePair);
				First: while (!TempDeque.isEmpty()) {
					if (TempDeque.peekRear().getSecond().equals("Subject")) {
						TempStack.push(TempDeque.deleteRear());
						while (!TempStack.isEmpty()) {
							Temp_String.append(TempStack.pop().getFirst());
						}
						Pair<String, String> Temp_Pair = new Pair(Temp_String.toString(), "ETM_Phrase");
						TempDeque.insertRear(Temp_Pair);
						Temp_String.delete(0, Temp_String.length());
						break First;
					} else {
						TempStack.push(TempDeque.deleteRear());
					}

				}
			} else {
				TempDeque.insertRear(temp);
				TempDeque.insertRear(SpacePair);
			}
		}
		while (!TempDeque.isEmpty()) {
			ReturnResult.add(TempDeque.deleteFront());
		}
		if (ReturnResult.get(ReturnResult.size() - 1).getFirst().equals("\b"))
			ReturnResult.remove(ReturnResult.size() - 1);
		return ReturnResult;
	}

	public List<Pair<String, String>> CombineNouns2(List<Pair<String, String>> Result) {
		StringBuffer Temp_String = new StringBuffer();
		List<Pair<String, String>> ReturnResult = new ArrayList<>();
		Deque NounQueue = new Deque();
		System.out.println("======================명사구 합치는 부분 인풋 출력======================");
		for (Pair<String, String> Temp : Result) {
			System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
		}
		for (Pair<String, String> Noun : Result) {
			if (Noun.getSecond().equals("NNP") || Noun.getSecond().equals("NNG") || Noun.getSecond().equals("NNB")
					|| Noun.getSecond().equals("JKG") || Noun.getSecond().equals("JC") || Noun.getFirst().equals("또는")
					|| Noun.getFirst().equals("다른") && !Noun.getSecond().equals("MAJ")) {
				NounQueue.insertRear(Noun);
			} else if (Noun.getSecond().equals("space") && !NounQueue.isEmpty()) {
				NounQueue.insertRear(Noun);
			} else if (Noun.getFirst().equals("및")) {
				NounQueue.insertRear(Noun);
			} else if (!Noun.getSecond().equals("space")) {
				while (!NounQueue.isEmpty()) {
					Temp_String.append(NounQueue.deleteFront().getFirst());
				}
				if (NounQueue.isEmpty() && Temp_String.length() != 0) {
					ReturnResult.add(new Pair(Temp_String.toString(), "NounPhrase"));
					ReturnResult.add(Noun);
				} else if (NounQueue.isEmpty() && Temp_String.length() == 0)
					ReturnResult.add(Noun);
				Temp_String.setLength(0);
			}
		}
		System.out.println("======================명사구 합치는 부분 아웃풋 출력======================");
		for (Pair<String, String> Temp : ReturnResult) {
			System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
		}
		return ReturnResult;
	}

	public List<List<Pair<String, String>>> DivideSentence2(List<Pair<String, String>> Result) {
		List<List<Pair<String, String>>> ReturnResult = new ArrayList<>();
		List<Pair<String, String>> Temp_List = new ArrayList<>();
		System.out.println("======================조사단위로 합쳐진 명사구 나누는 부분 인풋 출력======================");
		for (Pair<String, String> Temp : Result) {
			System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
		}
		for (Pair<String, String> Temp : Result) {
			Temp_List.add(Temp);
			if (Temp.getSecond().equals("JKS") || Temp.getSecond().equals("JX") || Temp.getSecond().equals("JKO")
					|| Temp.getSecond().equals("JKB") || Temp.getSecond().equals("MAJ")
					|| Temp.getSecond().equals("ETM") || Temp.getSecond().equals("MAG")
					|| Temp.getSecond().equals("EF")) {
				ReturnResult.add(ClonetheList(Temp_List));
				Temp_List.clear();
			}
		}
		System.out.println("======================조사단위로 합쳐진 명사구 나누는 부분 아웃풋 출력======================");
		for (Pair<String, String> Temp : Result) {
			System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
		}
		return ReturnResult;
	}

	public List<List<Pair<String, String>>> AnalyzeKTokens(List<List<Pair<String, String>>> Result) {
		List<List<Pair<String, String>>> ReturnResult = new ArrayList<>();
		System.out.println("======================조사 단위로 나눠진 명사구의 조사를 보고, 후보군을 예상하는 부분 인풋 출력======================");
		for (List<Pair<String, String>> Result1 : Result) {
			for (Pair<String, String> Temp : Result1) {
				System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
			}
		}
		for (int i = 0; i < Result.size(); i++) {
			List<Pair<String, String>> TempList = new ArrayList<>();
			for (int j = 0; j < Result.get(i).size(); j++) {
				Pair<String, String> Temp = new Pair(Result.get(i).get(j).getFirst(), Result.get(i).get(j).getSecond());
				TempList.add(Temp);
			}
			ReturnResult.add(TempList);
		}

		int i = 0;
		for (List<Pair<String, String>> Temp1 : Result) {
			for (Pair<String, String> Temp2 : Temp1) {
				if (Temp2.getSecond().equals("JKS") || Temp2.getSecond().equals("JX")) {
					Pair<String, String> Token = new Pair("Subject", "Subject");
					ReturnResult.get(i).add(Token);
				} else if (Temp2.getSecond().equals("JKO")) {
					Pair<String, String> Token = new Pair("Object", "Object");
					ReturnResult.get(i).add(Token);
				} else if (Temp2.getSecond().equals("JKB") || Temp2.getSecond().equals("MAJ")
						|| Temp2.getSecond().equals("MAG")) {
					Pair<String, String> Token = new Pair("Adverb", "Adverb");
					ReturnResult.get(i).add(Token);
				} else if (Temp2.getSecond().equals("ETM") && !Temp2.getFirst().equals("된")
						&& !Temp2.getFirst().equals("되는")) {
					Pair<String, String> Token = new Pair("ETM_Phrase", "ETM_Phrase");
					ReturnResult.get(i).add(Token);
				} else if (Temp2.getSecond().equals("ETM")
						&& (Temp2.getFirst().equals("된") || Temp2.getFirst().equals("되는"))) {
					Pair<String, String> Token = new Pair("Passive_ETM_Phrase", "Passive_ETM_Phrase");
					ReturnResult.get(i).add(Token);
				} else if (Temp2.getSecond().equals("EF")) {
					Pair<String, String> Token = new Pair("Verb", "Verb");
					ReturnResult.get(i).add(Token);
				}
			}
			i++;
		}
		System.out.println("======================조사 단위로 나눠진 명사구의 조사를 보고, 후보군을 예상하는 부분 인풋 출력======================");
		for (List<Pair<String, String>> Result1 : ReturnResult) {
			for (Pair<String, String> Temp : Result1) {
				System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
			}
		}
		return ReturnResult;
	}

	public List<Pair<String, String>> SetComponents(List<List<Pair<String, String>>> Result) {
		StringBuffer TempString = new StringBuffer();
		List<Pair<String, String>> ReturnResult = new ArrayList<>();
		System.out.println("======================SetComponents Step======================");
		System.out.println("======================후보군을 확실하게 정립하는 부분 인풋 출력======================");
		for (List<Pair<String, String>> Result1 : Result) {
			for (Pair<String, String> Temp : Result1) {
				System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
			}
		}
		for (int i = 0; i < Result.size(); i++) {
			if (Result.get(i).get(Result.get(i).size() - 1).getSecond().equals("Subject")) {
				Second: for (int j = 0; j < Result.get(i).size(); j++) {
					if (Result.get(i).get(j).getSecond().equals("Subject")) {
						break Second;
					} else {
						TempString.append(Result.get(i).get(j).getFirst());
					}
				}
				Pair<String, String> Temp_Token = new Pair(TempString.toString(), "Subject");
				ReturnResult.add(Temp_Token);
				TempString.delete(0, TempString.length());
			} else if (Result.get(i).get(Result.get(i).size() - 1).getSecond().equals("Object")) {
				Second2: for (int j = 0; j < Result.get(i).size(); j++) {
					if (Result.get(i).get(j).getSecond().equals("Object")) {
						break Second2;
					} else {
						TempString.append(Result.get(i).get(j).getFirst());
					}
				}
				Pair<String, String> Temp_Token = new Pair(TempString.toString(), "Object");
				ReturnResult.add(Temp_Token);
				TempString.delete(0, TempString.length());
			} else if (Result.get(i).get(Result.get(i).size() - 1).getSecond().equals("Adverb")) {
				Second3: for (int j = 0; j < Result.get(i).size(); j++) {
					if (Result.get(i).get(j).getSecond().equals("Adverb")) {
						break Second3;
					} else {
						TempString.append(Result.get(i).get(j).getFirst());
					}
				}
				Pair<String, String> Temp_Token = new Pair(TempString.toString(), "Adverb");
				ReturnResult.add(Temp_Token);
				TempString.delete(0, TempString.length());
			} else if (Result.get(i).get(Result.get(i).size() - 1).getSecond().equals("ETM_Phrase")) {
				Second4: for (int j = 0; j < Result.get(i).size(); j++) {
					if (Result.get(i).get(j).getSecond().equals("ETM_Phrase")) {
						break Second4;
					} else {
						TempString.append(Result.get(i).get(j).getFirst());
					}
				}
				Pair<String, String> Temp_Token = new Pair(TempString.toString(), "ETM_Phrase");
				ReturnResult.add(Temp_Token);
				TempString.delete(0, TempString.length());
			} else if (Result.get(i).get(Result.get(i).size() - 1).getSecond().equals("Verb")) {
				Second5: for (int j = 0; j < Result.get(i).size(); j++) {
					if (Result.get(i).get(j).getSecond().equals("Verb")) {
						break Second5;
					} else {
						TempString.append(Result.get(i).get(j).getFirst());
					}
				}
				Pair<String, String> Temp_Token = new Pair(TempString.toString() + ".", "Verb");
				ReturnResult.add(Temp_Token);
				TempString.delete(0, TempString.length());
			} else if (Result.get(i).get(Result.get(i).size() - 1).getSecond().equals("Passive_ETM_Phrase")) {
				Second6: for (int j = 0; j < Result.get(i).size(); j++) {
					if (Result.get(i).get(j).getSecond().equals("Passive_ETM_Phrase")) {
						break Second6;
					} else {
						TempString.append(Result.get(i).get(j).getFirst());
					}
				}
				Pair<String, String> Temp_Token = new Pair(TempString.toString(), "Passive_ETM_Phrase");
				ReturnResult.add(Temp_Token);
				TempString.delete(0, TempString.length());
			}
		}
		System.out.println("======================후보군을 확실하게 정립하는 부분 아웃풋 출력======================");
		for (Pair<String, String> Temp : ReturnResult) {
			System.out.println("[" + Temp.getFirst() + "/" + Temp.getSecond() + "]");
		}

		return ReturnResult;
	}

	public List<Pair<String, String>> Merger(List<Pair<String, String>> Result, int Start_Idx, int End_Idx) {
		return Result.subList(Start_Idx, End_Idx);
	}

	public List<Pair<String, String>> ClonetheList(List<Pair<String, String>> Temp_List) {
		List<Pair<String, String>> temp = new ArrayList<>();
		for (Pair<String, String> token : Temp_List) {
			temp.add(token);
		}
		return temp;
	}
}