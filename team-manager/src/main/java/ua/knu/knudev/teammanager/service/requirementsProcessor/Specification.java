package ua.knu.knudev.teammanager.service.requirementsProcessor;

public interface Specification<T> {

    boolean isSatisfiedBy(T t);
}
