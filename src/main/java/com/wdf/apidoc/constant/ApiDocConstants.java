package com.wdf.apidoc.constant;

/**
 * @author wangdingfu
 * @descption: 类型常量
 * @date 2022-04-10 21:42:57
 */
public interface ApiDocConstants {


    String VALUE = "value";

    interface ModifierProperty {
        String STATIC = "static";
        String FINAL = "final";
    }

    interface ExtInfo{
        String IS_ATTR = "isAttr";

        String GENERICS_TYPE = "genericsType";
    }

    interface ClassPkg {
        String BIG_DECIMAL = "java.math.BigDecimal";
        String TIMESTAMP = "java.sql.Timestamp";
        String LOCAL_TIME = "java.time.LocalTime";
        String LOCAL_DATE = "java.time.LocalDate";
        String LOCAL_DATE_TIME = "java.time.LocalDateTime";
        String BIG_INTEGER = "java.math.BigInteger";
    }


    interface Comment {

        /**
         * 请求参数的注释tag
         * TODO 后期可改成从配置中取
         */
        String PARAM = "param";
        /**
         * 方法返回值注释tag
         */
        String RETURN = "return";

        /**
         * psi中描述注释内容的类型
         */
        String PSI_COMMENT_DATA = "DOC_COMMENT_DATA";


        String COMMENT_START_1 = "/*";
        String COMMENT_START_2 = "//";
        String COMMENT_END_1 = "*/";

        String COMMENT_X = "*";
    }

}
