package com.vit.semesterresult;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "semester_results")
public class SemesterResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String studentName;
    private String prn;
    private double percentage;
    private double sgpa;
    private String grade;
    private String status;
    @Lob
    private String subjectsJson;

    protected SemesterResult() {
    }

    public SemesterResult(String studentName, String prn, double percentage, double sgpa,
                          String grade, String status, String subjectsJson) {
        this.studentName = studentName;
        this.prn = prn;
        this.percentage = percentage;
        this.sgpa = sgpa;
        this.grade = grade;
        this.status = status;
        this.subjectsJson = subjectsJson;
    }

    public Long getId() { return id; }
    public String getStudentName() { return studentName; }
    public String getPrn() { return prn; }
    public double getPercentage() { return percentage; }
    public double getSgpa() { return sgpa; }
    public String getGrade() { return grade; }
    public String getStatus() { return status; }
    public String getSubjectsJson() { return subjectsJson; }
}
