package com.jiu.injection.core;

import com.jiu.injection.annonation.InjectionField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 */
@Data
@AllArgsConstructor
public class FieldParam {
    private InjectionField anno;
    private Serializable queryKey;
    private Object curField;
}
