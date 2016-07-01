package com.badoo.hprof.library.model;

/**
 * Created by mfarouk on 6/3/16.
 */
public class StackFrame {

    private ID stackFrameId;
    private ID methodNameStringId;
    private ID methodSignatureStringId;
    private ID sourceFileNameStringId;
    private int classSerialNumber;
    private int lineNumber;


    public StackFrame()
    {

    }

    public StackFrame(ID stackFrameId, ID methodNameStringId, ID methodSignatureStringId, ID sourceFileNameStringId, int classSerialNumber, int lineNumber) {
        this.stackFrameId = stackFrameId;
        this.methodNameStringId = methodNameStringId;
        this.methodSignatureStringId = methodSignatureStringId;
        this.sourceFileNameStringId = sourceFileNameStringId;
        this.classSerialNumber = classSerialNumber;
        this.lineNumber = lineNumber;
    }

    public ID getStackFrameId() {
        return stackFrameId;
    }

    public void setStackFrameId(ID stackFrameId) {
        this.stackFrameId = stackFrameId;
    }

    public ID getMethodNameStringId() {
        return methodNameStringId;
    }

    public void setMethodNameStringId(ID methodNameStringId) {
        this.methodNameStringId = methodNameStringId;
    }

    public ID getMethodSignatureStringId() {
        return methodSignatureStringId;
    }

    public void setMethodSignatureStringId(ID methodSignatureStringId) {
        this.methodSignatureStringId = methodSignatureStringId;
    }

    public ID getSourceFileNameStringId() {
        return sourceFileNameStringId;
    }

    public void setSourceFileNameStringId(ID sourceFileNameStringId) {
        this.sourceFileNameStringId = sourceFileNameStringId;
    }

    public int getClassSerialNumber() {
        return classSerialNumber;
    }

    public void setClassSerialNumber(int classSerialNumber) {
        this.classSerialNumber = classSerialNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }


    @Override
    public String toString() {
        return "StackFrame{" +
                "stackFrameId=" + stackFrameId +
                ", methodNameStringId=" + methodNameStringId +
                ", methodSignatureStringId=" + methodSignatureStringId +
                ", sourceFileNameStringId=" + sourceFileNameStringId +
                ", classSerialNumber=" + classSerialNumber +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
