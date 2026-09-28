package cloudflight.integra.backend.finalgrade.model;

public record YearlyStatisticsDto(
    String academicYear,
    double averageGrade,
    double passRate,
    int totalStudents
) {
}
