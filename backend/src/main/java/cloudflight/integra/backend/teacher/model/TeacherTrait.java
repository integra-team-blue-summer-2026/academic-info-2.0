package cloudflight.integra.backend.teacher.model;

public enum TeacherTrait {
    OPTIMIZATION("Optimization"),
    ARTIFICIAL_INTELLIGENCE("Artificial Intelligence"),
    DATA_MINING("Data Mining"),
    COMPLEX_NETWORKS("Complex Networks"),
    IMAGE_PROCESSING("Image Processing"),
    MACHINE_LEARNING("Machine Learning"),
    SOFT_COMPUTING("Soft Computing"),
    SOFTWARE_ENGINEERING("Software Engineering"),
    SOFTWARE_QUALITY("Software Quality"),
    EMPIRICAL_METHODS_SE("Empirical Methods in Software Engineering"),
    COMPUTER_NETWORKS("Computer Networks"),
    CYBERSECURITY("Cybersecurity"),
    WEB_TECHNOLOGIES("Web Technologies"),
    MODULE_THEORY("Module Theory"),
    HOMOLOGICAL_ALGEBRA("Homological Algebra"),
    ABELIAN_GROUPS("Abelian Groups"),
    LINEAR_ALGEBRA("Linear Algebra"),
    FIXED_POINT_THEORY("Fixed Point Theory"),
    MULTIVALUED_OPERATOR_ANALYSIS("Multivalued Operator Analysis"),
    DIFFERENTIAL_EQUATIONS("Differential Equations"),
    GEOMETRY("Geometry"),
    DIFFERENTIAL_TOPOLOGY("Differential Topology"),
    ALGEBRAIC_TOPOLOGY("Algebraic Topology"),
    CRITICAL_POINTS("Critical Points"),
    NONLINEAR_ANALYSIS("Nonlinear Analysis"),
    NONLINEAR_DIFFERENTIAL_EQUATIONS("Nonlinear Differential Equations"),
    PARTIAL_DIFFERENTIAL_EQUATIONS("Partial Differential Equations"),
    FLUID_MECHANICS("Fluid Mechanics"),
    POTENTIAL_THEORY("Potential Theory"),
    COMPLEX_ANALYSIS("Complex Analysis");

    private final String displayName;

    TeacherTrait(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
