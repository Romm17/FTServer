package romanusyk.ft.repository;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.invoke.MethodHandles;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by Roman Usyk on 16.09.17.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class GroupRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GroupRepository groupRepository;

//    @Test
//    public void testFindByTitle() {
//
//        // TODO:
//        Group testGroup = new Group("megaTitle", "");
//        entityManager.persist(testGroup);
//        entityManager.flush();
//
//        assertThat(groupRepository.findByTitle("megaTitle")).isNotNull();
//        assertThat(groupRepository.findByTitle("megaTitle").getUserId()).isEqualTo(testGroup.getUserId());
//        assertThat(groupRepository.findByTitle("notSoMegaTitle")).isNull();
//
//        entityManager.remove(testGroup);
//
//    }
}
