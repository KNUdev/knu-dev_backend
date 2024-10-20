package ua.knu.knudev.query.dsl;

import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.knu.knudev.teammanager.domain.QAccountProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class QueryDslTest {

    private EntityManagerFactory factoryMock;

    private EntityManager mock;

    private JPAQueryFactory queryFactory;

    private JPQLQueryFactory queryFactory2;

    private JPAQueryFactory queryFactory3;

    private Map<String, Object> properties = new HashMap<>();

    @BeforeEach
    public void setUp() {
        factoryMock = EasyMock.createMock(EntityManagerFactory.class);
        mock = EasyMock.createMock(EntityManager.class);
        Supplier<EntityManager> provider = () -> mock;
        queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, provider);
        queryFactory2 = queryFactory;

        queryFactory3 = new JPAQueryFactory(provider);
    }

    @Test
    public void queryNotNullTest() {
        assertNotNull(queryFactory.query());
    }

    @Test
    void query2Test() {
        queryFactory2.query().from(QAccountProfile.accountProfile);
    }

    @Test
    void query3Test() {
        EasyMock.expect(mock.getEntityManagerFactory()).andReturn(factoryMock);
        EasyMock.expect(factoryMock.getProperties()).andReturn(properties);
        EasyMock.expect(mock.unwrap(EasyMock.anyObject(Class.class))).andReturn(mock).atLeastOnce();
        EasyMock.replay(mock, factoryMock);

        queryFactory3.query().from(QAccountProfile.accountProfile);
        EasyMock.verify(mock, factoryMock);
    }

    @Test
    public void fromFunctionTest() {
        assertNotNull(queryFactory.from(QAccountProfile.accountProfile));
    }

    @Test
    public void deleteFunctionTest1() {
        assertNotNull(queryFactory.delete(QAccountProfile.accountProfile));
    }

    @Test
    public void deleteFunctionTest2() {
        queryFactory2.delete(QAccountProfile.accountProfile)
                .where(QAccountProfile.accountProfile.firstName.length().gt(0));
    }

    @Test
    public void deleteFunctionTest3() {
        EasyMock.expect(mock.getEntityManagerFactory()).andReturn(factoryMock);
        EasyMock.expect(factoryMock.getProperties()).andReturn(properties);
        EasyMock.expect(mock.unwrap(EasyMock.anyObject(Class.class))).andReturn(mock).atLeastOnce();
        EasyMock.replay(mock, factoryMock);

        assertNotNull(queryFactory3.delete(QAccountProfile.accountProfile));
        EasyMock.verify(mock, factoryMock);
    }

    @Test
    public void updateFunctionTest1() {
        assertNotNull(queryFactory.update(QAccountProfile.accountProfile));
    }

    @Test
    public void updateFunctionTest2() {
        queryFactory2.update(QAccountProfile.accountProfile)
                .set(QAccountProfile.accountProfile.firstName, "Test firstname update")
                .where(QAccountProfile.accountProfile.firstName.length().gt(0));
    }

    @Test
    public void updateFunctionTest3() {
        EasyMock.expect(mock.getEntityManagerFactory()).andReturn(factoryMock);
        EasyMock.expect(factoryMock.getProperties()).andReturn(properties);
        EasyMock.expect(mock.unwrap(EasyMock.anyObject(Class.class))).andReturn(mock).atLeastOnce();
        EasyMock.replay(mock, factoryMock);

        assertNotNull(queryFactory3.update(QAccountProfile.accountProfile));
        EasyMock.verify(mock, factoryMock);
    }

    @Test
    public void insertFunctionTest1() {
        assertNotNull(queryFactory.insert(QAccountProfile.accountProfile));
    }

    @Test
    public void insertFunctionTest2() {
        queryFactory2.insert(QAccountProfile.accountProfile)
                .set(QAccountProfile.accountProfile.firstName, "Test firstname insert");
    }

    @Test
    public void insertFunctionTest3() {
        EasyMock.expect(mock.getEntityManagerFactory()).andReturn(factoryMock);
        EasyMock.expect(factoryMock.getProperties()).andReturn(properties);
        EasyMock.expect(mock.unwrap(EasyMock.anyObject(Class.class))).andReturn(mock).atLeastOnce();
        EasyMock.replay(mock, factoryMock);

        assertNotNull(queryFactory3.insert(QAccountProfile.accountProfile));
        EasyMock.verify(mock, factoryMock);
    }

}
