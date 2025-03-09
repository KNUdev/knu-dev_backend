package ua.knu.knudev.knudevcommon.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AccountAdministrativeRole implements AccountRole {
    SITE_MANAGER("Site Manager"),
    HEAD_MANAGER("Head Manager");

    private final String displayName;

    @Override
    public String getDisplayName() {
        return displayName;
    }
}

/*


Планування проектів - TECHLEAD+ !!!

Створення факультетів - HEAD MANAGER. Без пермішенів, постійно.

Створення тасок + тестів навчальних програм - PREMASTER+, і пермішін, і + відповідна технічна роль.
Створення тасок + тестів на підвищення ролі - MASTER+, і пермішін, і + відповідна технічна роль.
Створення програм - MASTER+ і пермішн.

Менеджмент любих тасок, тестів, навч.програм - юзер з відповідним тех.напрямком + пермішн.

Менеджмент юзерів - Manager+ і пермішн.

Менеджмент пермішінів - PERMISIION_MANAGER
а дасть пермішн - HEAD_MANAGER !!!


Як буде відбуватись перевірка тасок на навчанні?
Коли робиться нова навчальна сесія - ми руками призначаємо менторів.




 */