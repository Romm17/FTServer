package romanusyk.ft.utils.db;

import org.springframework.data.domain.Example;
import romanusyk.ft.data.entity.Group;

/**
 * Created by Roman Usyk on 15.11.17.
 */
public class GroupExampleBuilder {

    public Example<Group> buildExistingGroupExample(Group group) {
        Group exampleGroup = new Group();
        exampleGroup.setName(group.getName());
        return Example.of(exampleGroup);
    }

}
