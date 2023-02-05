/*
 * Copyright 2022 the GradleX team.
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradlex.buildparameters;

import java.util.Arrays;
import java.util.List;

interface Constants {
    String PACKAGE_NAME = "buildparameters";
    String PLUGIN_CLASS_NAME = "GeneratedBuildParametersPlugin";
    String GENERATED_EXTENSION_NAME = "buildParameters";
    String GENERATED_EXTENSION_CLASS_NAME = "BuildParametersExtension";
    List<String> JAVA_KEYWORDS = Arrays.asList( // 53 reserved words that cannot be used as Enum value
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double", "else", "enum", "extends", "false", "final", "finally", "float", "for", "goto",
            "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package",
            "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while"
    );
    List<Character> SPECIAL_IDENTIFIER_CHARACTERS = Arrays.asList(
            '.', '-'
    );
}
