package com.odigeo.interview.coding.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse implements Serializable {

    private static final long serialVersionUID = 3148790312805949137L;
    private Date timeStamp;
    private String message;
    private String detail;
}
