package com.lawrence.corejava.objectsandclasses;

public class RecordExample {
    // This record has a canonical constructor
    public record DefaultPoint(double x, double y){}

    // This record has non-canonical constructor
    public record Point(double x, double y){
        Point(){
            this(0.0, 0.0);
        }

        Point(double x) {
            this(x, 0.0);
        }
    }

    // this record has a customized canonical constructor
    public record Range(int min, int max) {
        public Range(int min, int max){
            if(min <= max){
                this.min = min;
                this.max = max;
            } else {
                this.min = max;
                this.max = min;
            }
        }
    }


}
