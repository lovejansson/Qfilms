package com.example.learndebugger;

import com.google.common.truth.ExpectFailure;
import com.google.common.truth.ThrowableSubject;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.rules.ExpectedException;


import static com.google.common.truth.Truth.assertThat;



public class CalculatorTest {
    private Calculator calculator;

    @Before
    public void setUp() {
        calculator = new Calculator();
    }

    @Test
    public void validAddition(){
       Integer res = calculator.calculateAnswer(100, 4, "Addition");
       assertThat(res).isEqualTo(104);
    }

    @Test
    public void validSubtraction(){
        Integer res = calculator.calculateAnswer(100, 4, "Subtraction");
        assertThat(res).isEqualTo(96);
    }

    @Test
    public void validDivision(){
        Integer res = calculator.calculateAnswer(100, 4, "Division");
        assertThat(res).isEqualTo(25);
    }

    @Test
    public void validMultiplication(){
        Integer res = calculator.calculateAnswer(100, 4, "Multiplication");
        assertThat(res).isEqualTo(400);
    }

    @Test
    public void inValidDivisionZero(){
        try{
            calculator.calculateAnswer(100, 0, "Division");

        }catch(Exception e){
            assertThat(e).isInstanceOf(ArithmeticException.class);
        }
    }
}