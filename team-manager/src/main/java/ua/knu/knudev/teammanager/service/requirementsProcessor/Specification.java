package ua.knu.knudev.teammanager.service.requirementsProcessor;

import ua.knu.knudev.teammanagerapi.requirements.PromotionRequirements;

public interface Specification<T extends PromotionRequirements> {

    boolean isSatisfiedBy(T t);

}
