package edu.columbia.main.language_id.cld;/*
 * Copyright 2014-present Deezer.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific languageCode governing permissions and
 * limitations under the License.
 */


import edu.columbia.main.language_id.*;
import edu.columbia.main.language_id.Result;

import java.io.IOException;

/**
 * Public interface for the CLD2 library.
 */
public class Cld2 extends LanguageClassifier {


    public Cld2() throws IOException, ClassNotFoundException {
        this.detectLanguage("test that will throw an error if fails to load cld");
        String [] listOfLangs = {"Afrikaans","Albanian","Arabic","Armenian","Azerbaijani","Basque","Belarusian","Bengali","Bihari languages","Bulgarian","Catalan","Cebuano","Cherokee","Croatian","Czech","Chinese","Danish","Dhivehi","Dutch","English","Estonian","Finnish","French","Galician","Ganda","Georgian","German","Modern Greek","Gujarati","Haitian","Hebrew","Hindi","Hmong","Hungarian","Icelandic","Indonesian","Inuktitut","Irish","Italian","Javanese","Japanese","Kannada","Central Khmer","Kinyarwanda","Korean","Lao","Latvian","Lithuanian","Macedonian","Malay","Malayalam","Maltese","Marathi","Nepali","Norwegian","Oriya","Persian","Polish","Portuguese","Panjabi","Romanian","Russian","Scottish Gaelic","Serbian","Sinhala","Slovak","Slovene","Spanish","Swahili","Swedish","Syriac","Tagalog","Tamil","Telugu","Thai","Turkish","Ukrainian","Urdu","Vietnamese","Welsh","Yiddish"};
        buildListOfSupportedLanguageCodesFromLanguageNames(listOfLangs);

    }

    public static int getLanguageFromName(String name) {
    return Cld2Library.INSTANCE._ZN4CLD219GetLanguageFromNameEPKc(name);
    }

    public static String getLanguageName(int language) {
    return Cld2Library.INSTANCE._ZN4CLD212LanguageNameENS_8LanguageE(language);
    }

    public static String getLanguageCode(int language) {
    return Cld2Library.INSTANCE._ZN4CLD212LanguageCodeENS_8LanguageE(language);
    }

    public static String version() {
    return Cld2Library.INSTANCE._ZN4CLD221DetectLanguageVersionEv();
    }



    public static edu.columbia.main.language_id.Result detect(String text) throws IOException, ClassNotFoundException{
      boolean isPlainText = true;

      CLDHints cldHints = new CLDHints(
              null,
              "",
              Encoding.UNKNOWN_ENCODING,
              Language.UNKNOWN_LANGUAGE);


      int flags = 0;
      int[] language3 = new int[3];
      int[] percent3 = new int[3];
      double[] normalizedScore3 = new double[3];
      int[] textBytes = new int[1];
      boolean[] isReliable = new boolean[1];
      byte[] utf8EncodedText;
      try {
          utf8EncodedText = text.getBytes("UTF-8");
      } catch (java.io.UnsupportedEncodingException exc) {
          return new edu.columbia.main.language_id.Result(null, false, 0);

      }
      int language = Cld2Library.INSTANCE._ZN4CLD224ExtDetectLanguageSummaryEPKcibPKNS_8CLDHintsEiPNS_8LanguageEPiPdPSt6vectorINS_11ResultChunkESaISA_EES7_Pb(
              utf8EncodedText,
              utf8EncodedText.length,
              isPlainText,
              cldHints,
              flags,
              language3,
              percent3,
              normalizedScore3,
              null, // Supposed to be a vector of ResultChunks, but it is not direct to pass vectors.
              textBytes,
              isReliable);

      LanguageCode lc = new LanguageCode(getLanguageCode(language), LanguageCode.CodeTypes.ISO_639_1);
      return new edu.columbia.main.language_id.Result(lc.getLanguageCode(), isReliable[0], percent3[0] / 100.0);

    }

    @Override
    public Result detectLanguage(String text) throws IOException, ClassNotFoundException {
        return Cld2.detect(text);
    }
}
