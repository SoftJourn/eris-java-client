package com.softjourn.eris.contract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.softjourn.eris.contract.types.Type;
import lombok.Data;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contract consists from some units (i.e functions, events)
 * This class is full representation of such unit
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractUnit {

    private String name;

    private boolean constant;

    private ContractUnitType type;

    private boolean anonymous;

    private Variable[] inputs;

    private Variable[] outputs;

    public String signature() {
        return Util.hash(name + "(" + commaSeparatedInputVars() + ")").substring(0, 8);
    }

    private String commaSeparatedInputVars() {
        return Stream
                .of(inputs)
                .map(Variable::getType)
                .map(Type::toString)
                .collect(Collectors.joining(","));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContractUnit that = (ContractUnit) o;

        if (constant != that.constant) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (type != that.type) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(inputs, that.inputs)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(outputs, that.outputs);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (constant ? 1 : 0);
        result = 31 * result + type.hashCode();
        result = 31 * result + Arrays.hashCode(inputs);
        result = 31 * result + Arrays.hashCode(outputs);
        return result;
    }
}
