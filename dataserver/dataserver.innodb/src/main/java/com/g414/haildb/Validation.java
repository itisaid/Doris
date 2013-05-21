package com.g414.haildb;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Validation {
    private static final Map<ColumnType, Validator<Object>> validators;
    static {
        Map<ColumnType, Validator<Object>> newValidators = new HashMap<ColumnType, Validator<Object>>();

        newValidators.put(ColumnType.VARCHAR, new Validator<Object>() {
            public boolean isValid(ColumnDef columnDef, Object obj) {
                if (isBadNull(columnDef, obj)) {
                    return false;
                } else if (obj == null) {
                    return true;
                }

                if (!(obj instanceof String)) {
                    return false;
                }

                String target = (String) obj;
                if (target != null && isBadLength(columnDef, target.length())) {
                    return false;
                }

                return true;
            };
        });

        newValidators.put(ColumnType.CHAR, newValidators
                .get(ColumnType.VARCHAR));

        newValidators.put(ColumnType.BINARY, new Validator<Object>() {
            public boolean isValid(ColumnDef columnDef, Object obj) {
                if (isBadNull(columnDef, obj)) {
                    return false;
                } else if (obj == null) {
                    return true;
                }

                byte[] target = (byte[]) obj;
                if (!(obj instanceof byte[])) {
                    return false;
                }

                if (target != null && isBadLength(columnDef, target.length)) {
                    return false;
                }

                return true;
            };
        });

        newValidators.put(ColumnType.VARBINARY, newValidators
                .get(ColumnType.BINARY));

        newValidators
                .put(ColumnType.BLOB, newValidators.get(ColumnType.BINARY));

        newValidators.put(ColumnType.INT, new Validator<Object>() {
            public boolean isValid(ColumnDef columnDef, Object obj) {
                if (isBadNull(columnDef, obj)) {
                    return false;
                } else if (obj == null) {
                    return true;
                }

                if (!(obj instanceof Number)) {
                    return false;
                }

                Number target = (Number) obj;

                if (!(target instanceof Byte) && !(target instanceof Short)
                        && !(target instanceof Integer)
                        && !(target instanceof Long)
                        && !(target instanceof BigInteger)) {
                    return false;
                }

                boolean isUnsigned = columnDef.is(ColumnAttribute.UNSIGNED);
                int signed = isUnsigned ? 0 : 1;
                int posBits = (8 * columnDef.getLength()) - signed;

                if (target instanceof BigInteger) {
                    BigInteger hiLimit = BigInteger.ONE.shiftLeft(posBits);
                    BigInteger loLimit = isUnsigned ? BigInteger.ZERO
                            : ((BigInteger) hiLimit).negate().subtract(
                                    BigInteger.ONE);

                    if (((BigInteger) target).compareTo(loLimit) < 0
                            || ((BigInteger) target).compareTo(hiLimit) > 0) {
                        return false;
                    }
                } else {
                    long hiLimit = Long.MAX_VALUE >> (63 - posBits);
                    long loLimit = isUnsigned ? 0 : -((Long) hiLimit) - 1;
                    long compare = target.longValue();

                    if ((compare < loLimit) || (compare > hiLimit)) {
                        return false;
                    }
                }

                return true;
            }
        });

        validators = Collections.unmodifiableMap(newValidators);
    }

    public static boolean isValid(ColumnDef def, Object value) {
        return validators.containsKey(def.getType())
                && validators.get(def.getType()).isValid(def, value);
    }

    private static boolean isBadNull(ColumnDef columnDef, Object target) {
        return columnDef.is(ColumnAttribute.NOT_NULL) && (target == null);
    }

    private static boolean isBadLength(ColumnDef columnDef, int length) {
        return columnDef.getLength() < length
                && !columnDef.getType().equals(ColumnType.BLOB);
    }

    private interface Validator<T> {
        public boolean isValid(ColumnDef columnDef, T target);
    }
}
