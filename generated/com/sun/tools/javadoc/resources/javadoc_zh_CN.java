package com.sun.tools.javadoc.resources;

import java.util.ListResourceBundle;

public final class javadoc_zh_CN extends ListResourceBundle {
    protected final Object[][] getContents() {
        return new Object[][] {
            { "javadoc.Body_missing_from_html_file", "HTML \u4E2D\u7F3A\u5C11\u4E3B\u4F53\u6807\u8BB0" },
            { "javadoc.End_body_missing_from_html_file", "HTML \u6587\u4EF6\u4E2D\u7F3A\u5C11\u4E3B\u4F53\u7ED3\u675F\u6807\u8BB0" },
            { "javadoc.File_Read_Error", "\u8BFB\u53D6\u6587\u4EF6 {0} \u65F6\u51FA\u9519" },
            { "javadoc.Multiple_package_comments", "\u627E\u5230\u8F6F\u4EF6\u5305 \"{0}\" \u7684\u591A\u4E2A\u8F6F\u4EF6\u5305\u6CE8\u91CA\u6E90" },
            { "javadoc.class_not_found", "\u627E\u4E0D\u5230\u7C7B {0}\u3002" },
            { "javadoc.error", "\u9519\u8BEF" },
            { "javadoc.warning", "\u8B66\u544A" },
            { "main.Building_tree", "\u6B63\u5728\u6784\u9020 Javadoc \u4FE1\u606F..." },
            { "main.Loading_source_file", "\u6B63\u5728\u88C5\u5165\u6E90\u6587\u4EF6 {0}..." },
            { "main.Loading_source_file_for_class", "\u6B63\u5728\u88C5\u5165\u7C7B {0} \u7684\u6E90\u6587\u4EF6..." },
            { "main.Loading_source_files_for_package", "\u6B63\u5728\u88C5\u5165\u8F6F\u4EF6\u5305 {0} \u7684\u6E90\u6587\u4EF6..." },
            { "main.No_packages_or_classes_specified", "\u672A\u6307\u5B9A\u8F6F\u4EF6\u5305\u6216\u7C7B\u3002" },
            { "main.cant.read", "\u65E0\u6CD5\u8BFB\u53D6 {0}" },
            { "main.doclet_class_not_found", "\u627E\u4E0D\u5230 doclet \u7C7B {0}" },
            { "main.doclet_method_must_be_static", "\u5728 doclet \u7C7B {0} \u4E2D\uFF0C\u65B9\u6CD5 {1} \u5FC5\u987B\u4E3A\u9759\u6001\u3002" },
            { "main.doclet_method_not_accessible", "\u5728 doclet \u7C7B {0} \u4E2D\uFF0C\u65E0\u6CD5\u8BBF\u95EE\u65B9\u6CD5 {1}" },
            { "main.doclet_method_not_found", "doclet \u7C7B {0} \u4E0D\u5305\u542B {1} \u65B9\u6CD5" },
            { "main.done_in", "[\u5728{0} \u6BEB\u79D2\u5185\u5B8C\u6210]" },
            { "main.error", "{0} \u9519\u8BEF" },
            { "main.errors", "{0} \u9519\u8BEF" },
            { "main.exception_thrown", "\u5728 doclet \u7C7B {0} \u4E2D\uFF0C\u65B9\u6CD5 {1} \u5DF2\u629B\u51FA\u5F02\u5E38 {2}" },
            { "main.fatal.error", "\u81F4\u547D\u9519\u8BEF" },
            { "main.fatal.exception", "\u81F4\u547D\u5F02\u5E38" },
            { "main.file_not_found", "\u627E\u4E0D\u5230\u6587\u4EF6\uFF1A\"{0}\"" },
            { "main.illegal_locale_name", "\u8BED\u8A00\u73AF\u5883\u4E0D\u53EF\u7528\uFF1A{0}" },
            { "main.illegal_package_name", "\u975E\u6CD5\u7684\u8F6F\u4EF6\u5305\u540D\u79F0\uFF1A\"{0}\"" },
            { "main.incompatible.access.flags", "\u6307\u5B9A\u4E86\u591A\u4E2A -public\u3001-private\u3001-package \u6216 -protected\u3002" },
            { "main.internal_error_exception_thrown", "\u5185\u90E8\u9519\u8BEF\uFF1A\u5728 doclet \u7C7B {0} \u4E2D\uFF0C\u65B9\u6CD5 {1} \u5DF2\u629B\u51FA\u5F02\u5E38 {2}" },
            { "main.invalid_flag", "\u65E0\u6548\u7684\u6807\u5FD7\uFF1A {0}" },
            { "main.locale_first", "\u5728\u547D\u4EE4\u884C\u4E2D\uFF0C\u9009\u9879 -locale \u5FC5\u987B\u4E3A\u7B2C\u4E00\u4E2A\u9009\u9879\u3002" },
            { "main.malformed_locale_name", "\u4E0D\u89C4\u5219\u7684\u8BED\u8A00\u73AF\u5883\u540D\u79F0\uFF1A{0}" },
            { "main.more_than_one_doclet_specified_0_and_1", "\u6307\u5B9A\u4E86\u591A\u4E2A doclet\uFF08{0} \u548C {1}\uFF09\u3002" },
            { "main.must_return_boolean", "\u5728 doclet \u7C7B {0} \u4E2D\uFF0C\u65B9\u6CD5 {1} \u5FC5\u987B\u8FD4\u56DE\u5E03\u5C14\u503C\u3002" },
            { "main.must_return_int", "\u5728 doclet \u7C7B {0} \u4E2D\uFF0C\u65B9\u6CD5 {1} \u5FC5\u987B\u8FD4\u56DE\u6574\u578B\u503C\u3002" },
            { "main.must_return_languageversion", "\u5728 doclet \u7C7B {0} \u4E2D\uFF0C\u65B9\u6CD5 {1} \u5FC5\u987B\u8FD4\u56DE\u8BED\u8A00\u7248\u672C\u3002" },
            { "main.no_source_files_for_package", "\u6CA1\u6709\u8F6F\u4EF6\u5305 {0} \u7684\u6E90\u6587\u4EF6" },
            { "main.option.already.seen", "{0} \u9009\u9879\u53EF\u80FD\u88AB\u6307\u5B9A\u4E86\u4E0D\u6B62\u4E00\u6B21\u3002" },
            { "main.out.of.memory", "java.lang.OutOfMemoryError\uFF1A\u8BF7\u589E\u52A0\u5185\u5B58\u3002\n\u4F8B\u5982\uFF0C\u5BF9\u4E8E Sun Classic \u6216 HotSpot VM\uFF0C\u8BF7\u6DFB\u52A0\u9009\u9879 -J-Xmx\uFF0C\n\u5982 -J-Xmx32m\u3002" },
            { "main.requires_argument", "\u9009\u9879 {0} \u9700\u8981\u53C2\u6570\u3002" },
            { "main.usage", "\u7528\u6CD5\uFF1Ajavadoc [\u9009\u9879] [\u8F6F\u4EF6\u5305\u540D\u79F0] [\u6E90\u6587\u4EF6] [@file]\n-overview <\u6587\u4EF6>          \u8BFB\u53D6 HTML \u6587\u4EF6\u7684\u6982\u8FF0\u6587\u6863\n-public                   \u4EC5\u663E\u793A\u516C\u5171\u7C7B\u548C\u6210\u5458\n-protected                \u663E\u793A\u53D7\u4FDD\u62A4/\u516C\u5171\u7C7B\u548C\u6210\u5458\uFF08\u9ED8\u8BA4\uFF09\n-package                  \u663E\u793A\u8F6F\u4EF6\u5305/\u53D7\u4FDD\u62A4/\u516C\u5171\u7C7B\u548C\u6210\u5458\n-private                  \u663E\u793A\u6240\u6709\u7C7B\u548C\u6210\u5458\n-help                     \u663E\u793A\u547D\u4EE4\u884C\u9009\u9879\u5E76\u9000\u51FA\n-doclet <\u7C7B>              \u901A\u8FC7\u66FF\u4EE3 doclet \u751F\u6210\u8F93\u51FA\n-docletpath <\u8DEF\u5F84>        \u6307\u5B9A\u67E5\u627E doclet \u7C7B\u6587\u4EF6\u7684\u4F4D\u7F6E\n-sourcepath <\u8DEF\u5F84\u5217\u8868>    \u6307\u5B9A\u67E5\u627E\u6E90\u6587\u4EF6\u7684\u4F4D\u7F6E\n-classpath <\u8DEF\u5F84\u5217\u8868>     \u6307\u5B9A\u67E5\u627E\u7528\u6237\u7C7B\u6587\u4EF6\u7684\u4F4D\u7F6E\n-exclude <\u8F6F\u4EF6\u5305\u5217\u8868>     \u6307\u5B9A\u8981\u6392\u9664\u7684\u8F6F\u4EF6\u5305\u7684\u5217\u8868\n-subpackages <\u5B50\u8F6F\u4EF6\u5305\u5217\u8868> \u6307\u5B9A\u8981\u9012\u5F52\u88C5\u5165\u7684\u5B50\u8F6F\u4EF6\u5305\n-breakiterator            \u4F7F\u7528 BreakIterator \u8BA1\u7B97\u7B2C 1 \u53E5\n-bootclasspath <\u8DEF\u5F84\u5217\u8868> \u8986\u76D6\u5F15\u5BFC\u7C7B\u52A0\u8F7D\u5668\u6240\u88C5\u5165\u7684\n\t\t\t  \u7C7B\u6587\u4EF6\u7684\u4F4D\u7F6E\n-source <\u7248\u672C>            \u63D0\u4F9B\u4E0E\u6307\u5B9A\u7248\u672C\u7684\u6E90\u517C\u5BB9\u6027\n-extdirs <\u76EE\u5F55\u5217\u8868>       \u8986\u76D6\u5B89\u88C5\u7684\u6269\u5C55\u76EE\u5F55\u7684\u4F4D\u7F6E\n-verbose                  \u8F93\u51FA\u6709\u5173 Javadoc \u6B63\u5728\u6267\u884C\u7684\u64CD\u4F5C\u7684\u6D88\u606F\n-locale <\u540D\u79F0>            \u8981\u4F7F\u7528\u7684\u8BED\u8A00\u73AF\u5883\uFF0C\u4F8B\u5982 en_US \u6216 en_US_WIN\n-encoding <\u540D\u79F0>          \u6E90\u6587\u4EF6\u7F16\u7801\u540D\u79F0\n-quiet                    \u4E0D\u663E\u793A\u72B6\u6001\u6D88\u606F\n-J<\u6807\u5FD7>                  \u76F4\u63A5\u5C06 <\u6807\u5FD7> \u4F20\u9012\u7ED9\u8FD0\u884C\u65F6\u7CFB\u7EDF\n" },
            { "main.warning", "{0} \u8B66\u544A" },
            { "main.warnings", "{0} \u8B66\u544A" },
            { "tag.End_delimiter_missing_for_possible_SeeTag", "\u6CE8\u91CA\u5B57\u7B26\u4E32\u4E2D\u53EF\u80FD\u51FA\u73B0\u7684\u53C2\u89C1\u6807\u8BB0\u7F3A\u5C11\u7ED3\u675F\u5206\u9694\u7B26 }\uFF1A\"{0}\"" },
            { "tag.Improper_Use_Of_Link_Tag", "\u5185\u5D4C\u6807\u8BB0\u7F3A\u5C11\u7ED3\u675F \"}\" \u5B57\u7B26\uFF1A\"{0}\"" },
            { "tag.illegal_char_in_arr_dim", "\u6807\u8BB0 {0}\uFF1A\u6570\u7EC4\u7EF4\u6570\u3001\u65B9\u6CD5\u53C2\u6570\u4E2D\u6709\u8BED\u6CD5\u9519\u8BEF\uFF1A{1}" },
            { "tag.illegal_see_tag", "\u6807\u8BB0 {0}\uFF1A\u65B9\u6CD5\u53C2\u6570\u4E2D\u6709\u8BED\u6CD5\u9519\u8BEF\uFF1A{1}" },
            { "tag.missing_comma_space", "\u6807\u8BB0 {0}\uFF1A\u65B9\u6CD5\u53C2\u6570\u4E2D\u7F3A\u5C11\u9017\u53F7\u6216\u7A7A\u683C\uFF1A{1}" },
            { "tag.see.can_not_find_member", "\u6807\u8BB0 {0}\uFF1A\u5728 {2} \u4E2D\u627E\u4E0D\u5230 {1}" },
            { "tag.see.class_not_found", "\u627E\u4E0D\u5230 @see \u6807\u8BB0\u7684\u7C7B {0}\uFF1A\"{1}\"" },
            { "tag.see.class_not_specified", "\u6807\u8BB0 {0}\uFF1A\u672A\u6307\u5B9A\u7C7B\uFF1A\"{1}\"" },
            { "tag.see.illegal_character", "\u6807\u8BB0 {0}\uFF1A\"{2}\" \u4E2D\u7684 \"{1}\" \u4E3A\u975E\u6CD5\u5B57\u7B26" },
            { "tag.see.malformed_see_tag", "\u6807\u8BB0 {0}\uFF1A\u4E0D\u89C4\u5219\uFF1A\"{1}\"" },
            { "tag.see.missing_sharp", "\u6807\u8BB0 {0}\uFF1A\u7F3A\u5C11 \"#\"\uFF1A\"{1}\"" },
            { "tag.see.no_close_bracket_on_url", "\u6807\u8BB0 {0}\uFF1A\u7F3A\u5C11\u6700\u540E\u7684 \">\"\uFF1A\"{1}\"" },
            { "tag.see.no_close_quote", "\u6807\u8BB0 {0}\uFF1A\u65E0\u53F3\u5F15\u53F7\uFF1A\"{1}\"" },
            { "tag.serialField.illegal_character", "@serialField \u6807\u8BB0\u4E2D\u7684\u975E\u6CD5\u5B57\u7B26 {0}\uFF1A{1}\u3002" },
            { "tag.tag_has_no_arguments", "{0} \u6807\u8BB0\u6CA1\u6709\u53C2\u6570\u3002" },
            { "tag.throws.exception_not_found", "\u6807\u8BB0 {0}\uFF1A\u627E\u4E0D\u5230\u7C7B {1}\u3002" },
        };
    }
}