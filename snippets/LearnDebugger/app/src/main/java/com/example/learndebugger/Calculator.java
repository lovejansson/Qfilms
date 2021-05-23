package com.example.learndebugger;

public class Calculator {

    protected Integer calculateAnswer(Integer one, Integer two, String op) {

        Integer answer;
        switch(op){
            case "Subtraction":
                answer = one - two;
                break;
            case "Multiplication":
                answer = one * two;
                break;
            case "Division":
                answer = one / two;
                break;
            default:
                answer = one + two;
                break;

        }

        return answer;
    }

}
