package cloudflight.integra.backend.studentexam.model;

public record ExamYearlyStatisticsDto(
    String academicYear,
    double sessionAverage,
    double sessionPassRate,
    double resitAverage,
    double resitPassRate,
    double overhaulPassRate,
    int totalStudents
) {
}
