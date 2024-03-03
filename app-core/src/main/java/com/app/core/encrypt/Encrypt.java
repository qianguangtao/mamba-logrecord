package com.app.core.encrypt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 加密对象
 *
 * @author qiangt
 * @since 2022-10-22
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Encrypt implements Serializable {

    private String value;

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Encrypt) {
            return Objects.equals(this.value, ((Encrypt) o).getValue());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.value;
    }

}
