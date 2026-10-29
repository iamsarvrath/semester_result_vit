package com.vit.semesterresult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "*")
public class SemesterResultController {
    private final SemesterResultRepository repository;
    private final ObjectMapper objectMapper;

    public SemesterResultController(SemesterResultRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse createResult(@RequestBody ResultRequest request) {
        validate(request);
        double totalCredits = request.subjects().stream().mapToDouble(SubjectMark::credits).sum();
        double percentage = request.subjects().stream()
                .mapToDouble(subject -> subject.score() * subject.credits())
                .sum() / totalCredits;
        double sgpa = percentage / 10;
        String[] gradeDetails = gradeFor(percentage);

        try {
            String subjectsJson = objectMapper.writeValueAsString(request.subjects());
            SemesterResult saved = repository.save(new SemesterResult(
                    request.studentName(), request.prn(), round(percentage), round(sgpa),
                    gradeDetails[0], gradeDetails[1], subjectsJson));
            return new ResultResponse(saved.getId(), saved.getStudentName(), saved.getPercentage(),
                    saved.getSgpa(), saved.getGrade(), saved.getStatus(), request.subjects());
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not save subjects", exception);
        }
    }

    private void validate(ResultRequest request) {
        if (request.studentName() == null || request.studentName().isBlank()
                || request.prn() == null || request.prn().isBlank()
                || request.subjects() == null || request.subjects().size() != 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter student details and exactly four subjects");
        }
        request.subjects().forEach(subject -> {
            if (subject.name() == null || subject.name().isBlank()
                    || subject.mse() < 0 || subject.mse() > 30
                    || subject.ese() < 0 || subject.ese() > 70
                    || subject.credits() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Marks or credits are invalid");
            }
        });
    }

    private String[] gradeFor(double percentage) {
        if (percentage >= 90) return new String[]{"O", "Outstanding performance"};
        if (percentage >= 80) return new String[]{"A+", "Excellent performance"};
        if (percentage >= 70) return new String[]{"A", "Very good performance"};
        if (percentage >= 60) return new String[]{"B+", "Good performance"};
        if (percentage >= 50) return new String[]{"B", "Passed"};
        if (percentage >= 40) return new String[]{"C", "Passed"};
        return new String[]{"F", "Needs improvement"};
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public record ResultRequest(String studentName, String prn, List<SubjectMark> subjects) {}

    public record SubjectMark(String name, double mse, double ese, int credits) {
        public double score() {
            return mse + ese;
        }
    }

    public record ResultResponse(Long id, String studentName, double percentage, double sgpa,
                                 String grade, String status, List<SubjectMark> subjects) {}
}
