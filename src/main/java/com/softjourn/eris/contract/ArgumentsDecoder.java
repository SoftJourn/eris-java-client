package com.softjourn.eris.contract;

import com.softjourn.eris.contract.types.Type;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.softjourn.eris.contract.Util.encodeInt;

/**
 * Class to encode/decode java types to Eris(Solidity) string representation
 */
public class ArgumentsDecoder {

    /**
     * Read Eris response string as out parameters
     * @param unit Contract unit (i.e. function, event)
     * @param value response string
     * @return List of Java object that represents appropriate Eris(Solidity) types
     */
    public List<Object> readArgs(@NonNull ContractUnit unit, String value) {
        Variable[] variables = unit.getOutputs();
        int[] offsets = getOffsets(variables);
        return IntStream.range(0, variables.length)
                .mapToObj(i -> processType(variables[i].getType(), offsets[i], value))
                .collect(Collectors.toList());
    }


    public List<Object> readInputArgs(@NonNull ContractUnit unit, String value) {
        Variable[] variables = unit.getInputs();
        int[] offsets = getOffsets(variables);
        return IntStream.range(0, variables.length)
                .mapToObj(i -> processType(variables[i].getType(), offsets[i], value))
                .collect(Collectors.toList());
    }


    /**
     * Write arguments of specified ContractUnit (function) to Eris(Solidity) formatted string
     * @param unit Contract unit (i.e. function)
     * @param args Arguments to be formatted
     *             Must be exact as function need count and types
     * @return formatted string
     */
    @SuppressWarnings("unchecked")
    public String writeArgs(@NonNull ContractUnit unit, Object... args) {
        checkLengthCorrectness(unit, args);
        StringBuilder res = new StringBuilder();

        int dynamicOffset = Stream.of(unit.getInputs())
                .map(Variable::getType)
                .map(Type::staticPartLength)
                .map(l -> ((l + 31) / 32) * 32)
                .reduce(0, (x, y) -> x + y);

        StringBuilder dynamicData = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            checkTypeCorrectness(unit, args, i);
            Type type = unit.getInputs()[i].getType();

            String encodedValue = type.formatInput(args[i]);
            if (type.isDynamic()) {
                res.append(encodeInt(dynamicOffset));
                dynamicOffset += encodedValue.length() / 2;
                dynamicData.append(encodedValue);
            } else {
                res.append(encodedValue);
            }
        }
        return res.append(dynamicData).toString().toUpperCase();
    }

    private int[] getOffsets(Variable[] variables) {
        int[] lengths = Stream.of(variables)
                .map(Variable::getType)
                .mapToInt(Type::staticPartLength)
                .map(this::roundTo32)
                .toArray();
        for (int i = 0; i < lengths.length; i++) {
            lengths[i] += i== 0 ? 0 : lengths[i-1];
        }
        for (int i = 0; i < lengths.length; i++) {
            lengths[i] -= roundTo32(variables[i].getType().staticPartLength());
        }
        return lengths;
    }

    private int roundTo32(int value) {
        return  ((value + 31) / 32) * 32;
    }

    private Object processType(Type type, int offset, String value) {
        //TODO now only arrays of static types supported. Add supporting arrays of dynamic types(bytes, arrays)
        if (type.isDynamic()) {
            return formatDynamicType(type, offset, value);
        } else {
            return formatStaticType(type, offset, value);
        }
    }

    private Object formatDynamicType(Type type, int offset, String value) {
        //find this value offset
        int dynamicOffset = Integer.parseInt(value.substring(offset * 2, offset * 2 + 64), 16);
        //find this value length
        int length = Integer.parseInt(value.substring(dynamicOffset * 2, dynamicOffset * 2 + 64),16);
        //all values is padded to 32 bytes (64 hex chars)
        //find how many 32 byte parts contains this value
        int roundedLength = (length * type.staticPartLength() + 31) / 32;
        //get current value string from whole string
        String currentValueString = value.substring(dynamicOffset * 2, dynamicOffset * 2 + ( 1 + roundedLength) * 64);
        //parse value by pojo
        return type.formatOutput(currentValueString);
    }

    private Object formatStaticType(Type type, int offset, String value) {
        int startOffset = offset * 2;
        int endOffSet =  (offset + type.staticPartLength()) * 2;
        if (value.length() < startOffset || value.length() < endOffSet) {
            throw new IllegalArgumentException("Can't parse " + type + " from response. Wrong response length or offsets.");
        }
        String currentValue = value.substring(startOffset, endOffSet);
        return type.formatOutput(currentValue);
    }

    private void checkLengthCorrectness(ContractUnit unit, Object... args) {
        if (unit.getInputs().length != args.length) {
            throw new IllegalArgumentException("Count of args in function " + unit.getName() +
                    " is " + unit.getInputs().length +
                    " but was passed " + args.length);
        }
    }

    private void checkTypeCorrectness(ContractUnit unit, Object[] args, int index) {
        if (!unit.getInputs()[index].getType().valueClass().isInstance(args[index])) {
            throw new IllegalArgumentException("The " + (index + 1) + "-th parameter of function " + unit.getName() +
                    " is " + unit.getInputs()[index].getType().toString() + " but argument was " + args[index]);
        }
    }
}
