package demo2;

import java.util.HashMap;
import java.util.List;

public class Grade {
    private String name;
    private List<Student> students;
    private List<HashMap> map;
    
    public Grade(){}
    public Grade(String name,List<Student> students){
        this.name=name;
        this.students=students;
    }
    public String toString(){
        StringBuilder sb=new StringBuilder();
        for(Student s : students){
            sb.append(s.getNumberid()+"    ");
        }
        return sb.toString();
    }
    
    
    
   static class Student {
        private String name;
        private int age;
        private String numberid;
        public String getNumberid(){
            return numberid;
        }
    }
}