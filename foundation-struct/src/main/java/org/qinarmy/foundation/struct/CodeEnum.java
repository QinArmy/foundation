package org.qinarmy.foundation.struct;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public interface CodeEnum extends Compare<CodeEnum> {


    /**
     * @return 用于持久化到数据库中的 code
     */
    int code();

    /**
     * 枚举的 name
     */
    String name();

    /**
     * @return 用于展示到前端的名称
     */
    String display();

    default String localizedDisplay() {
        return display();
    }

    default CodeEnum family() {
        return this;
    }


    /*################# static method ############################*/

    @Override
    default CompareResult compare(CodeEnum o) {
        return compare(this, o);
    }


    static CompareResult compare(CodeEnum codeEnum1, CodeEnum codeEnum2) {
        return CompareResult.resolve(codeEnum1.code() - codeEnum2.code());
    }


    /**
     * @throws IllegalArgumentException enum error
     */
    static <T extends Enum<T> & CodeEnum> Map<Integer, T> getCodeMap(Class<T> clazz) {
        CodeEnumHelper.assertCodeEnum(clazz);

        Map<Integer, T> map = CodeEnumHelper.getMap(clazz);

        if (map != null) {
            return map;
        }

        T[] types = clazz.getEnumConstants();
        map = new HashMap<>((int) (types.length / 0.75f));

        for (T type : types) {
            if (map.containsKey(type.code())) {
                throw new IllegalArgumentException(
                        String.format("Enum[%s] code[%s]duplicate", clazz.getName(), type.code()));
            }
            map.put(type.code(), type);
        }
        map = Collections.unmodifiableMap(map);
        CodeEnumHelper.addMap(clazz, map);
        return map;
    }

}
