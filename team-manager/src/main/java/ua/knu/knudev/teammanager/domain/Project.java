package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.knudevcommon.constant.ProjectTag;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "team_management", name = "project")
@Builder
@BatchSize(size = 10)
public class Project {

    /*
    Девелопер->Премастер
1)кількість комітів в усіх проєктах кампуса в мастер має бути більша за N

Премастер->Мастер
1)кількість комітів в усіх проектах кампуса в мастер має бути більша за 2N
2)проєкт який розробляється командою премастера має мати хоча б 1 завершену версію

... Це премастер заходить в проект -
Значить треба ідею свапів по проектам. Свапати треба рпи релізах.

Мастер->Техлід
1-проєкт який веде мастер має мати хоча б 2 успішно завершені версії
2-має мати в прекампусі хоча б 2 студентів яких він менторує
3-має провести як мінімум 1 мастер-клас в прекампусі/кампус (або сттаі написані)


     */
/*
    ПИТАННЯ

    1. Свапати разрабів при релізах. А що, якщо чувак приєднався до проекту посеред релізу?
    2. На мастера, техліда трекати кількість зроблених тестів і завдань.
 */


    /*
        1. Треба мати можливість трекати, яку роль мав юзер, коли він зайшов до проекту, щоб у майбутньому на
        підвищення ролі дивитися, скільки є готових релізів.

        Премастер - Мастер
        Повинен брати участь у проектах, у яких відбувся реліз.

        Значить чувак зайшов у проект - і при його присутствії повинен відбутися реліз.

        Значить. У проектах зберігається відсортовваний список релізів.
        Коли робиться новий реліз,

     */

    /*
        Що треба:
        Проект складаєьтся з підпроектів. У кожного під-проекта є свій неунікальний напрямок. Бекенд, фронт ен



        1. Зберігати список релізів. У кожному релізі є дата, коли релізнувся. І мб люди
        2. Наявність 1 архітектора, скільки завгодно мастерів, скільки завгодно пре-мастерів, скільки заговдно девелоперів
        3.

     */

    //Some way to track is project has "completed" or is it under support. Some status
    // type. Website, API server, etc. Remember, this is independed project
    //AccountProfile architect. Is an arhitect. Non-nullable
    // Supervisor. Who "mentors" architect. Nullable
    //Stakeholder ???
    // List<ProjectPart> 2way. Each part contains type(backend, frontend, ui-ux etc). not nullable
    //Each ProjectPart has :
    // 1. Master developers (List<AccountProfile> masterDevelopers ?)
    // 2. Premaster developers
    // 3. Developers.  What if the roles will change?
    // 4. Github link
    // 5. List<Release> releases. 2way. Have release date. Each release has:
    // 1) Need some way to track succeeded releases at developers. So need to take snapshot of users
    // 2) Who is the releaser
    // 3) Need to have "active" and "total" developers total developers are not in release, but in ProjectPart

    // Does ProjectPart need to have name?
    // How to store design link? Or even need to store?




    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "en", column = @Column(name = "name_en")),
            @AttributeOverride(name = "uk", column = @Column(name = "name_uk"))
    })
    private MultiLanguageField name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "en", column = @Column(name = "description_en")),
            @AttributeOverride(name = "uk", column = @Column(name = "description_uk"))
    })
    private MultiLanguageField description;

    @Column
    private String avatarFilename;

    @Column
    private LocalDate startedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @BatchSize(size = 40)
    @ElementCollection(targetClass = ProjectTag.class)
    @CollectionTable(schema = "team_management", name = "tag", joinColumns = @JoinColumn(name = "project_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tags", nullable = false)
    private Set<ProjectTag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "architect_account_id", referencedColumnName = "id", nullable = false)
    private AccountProfile architect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_account_id", referencedColumnName = "id")
    private AccountProfile supervisor;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Subproject> subprojects = new HashSet<>();

    //todo rename this to represent totsl developers
    //todo sort by commit count, maybe add total fueld commit count
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectAccount> projectAccounts = new HashSet<>();

//    @BatchSize(size = 20)
//    @ElementCollection
//    @CollectionTable(
//            schema = "team_management",
//            name = "github_repo_links",
//            joinColumns = @JoinColumn(name = "project_id")
//    )
//    @Column(name = "github_repo_link", nullable = false)
//    private Set<String> githubRepoLinks = new HashSet<>();
//
//    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Release releaseInfo;
//


}
