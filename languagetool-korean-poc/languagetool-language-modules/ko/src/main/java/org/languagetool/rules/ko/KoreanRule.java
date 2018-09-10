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
package org.languagetool.rules.ko;

import org.languagetool.language.Korean;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.rules.ko.StandardSentence;



import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;

/**
 * A rule that matches words which should not be used and suggests correct ones instead. 
 * Khmer implementations. Loads the list of words from
 * <code>/km/coherency.txt</code>.
 */



public class KoreanRule extends Rule {

  public static final String KOREAN_TEMPLATE_RULES = "KO_TEMPLATE_RULE";

  private static final Locale KO_LOCALE = new Locale("ko");  // locale used on case-conversion
  

  @Override
  public final String getId() {
    return KOREAN_TEMPLATE_RULES;
  }

  @Override
  public String getDescription() {
    return "Words or groups of words that are incorrect or obsolete";
  }

  //This is the method to detect whether this sentence is wrong with Template or not
  @Override
  public RuleMatch[] match(AnalyzedSentence sentence) throws IOException {
	  List<RuleMatch> ruleMatches = new ArrayList<>();
	  StandardSentence cusor = new StandardSentence();
	  //Getting all the tokens (i.e. words) of this sentence, but there is no space
	  AnalyzedTokenReadings[] tokens = sentence.getTokensWithoutWhitespace();
	  
	  //First token will be a token of the start of a sentence 
	 // RuleMatch ruleMatch = new RuleMatch(this, tokens[cusor.FindSubjectStartIdx(sentence,cusor.FindSubjectEndIdx(sentence))].getStartPos(), tokens[cusor.FindSubjectEndIdx(sentence)].getEndPos(),"주어부(자바)","주어찾는 룰입니다.");
	  //ruleMatches.add(ruleMatch);
	return toRuleMatchArray(ruleMatches);
  }

}
