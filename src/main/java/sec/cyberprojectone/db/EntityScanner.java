package sec.cyberprojectone.db;

import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

class EntityScanner {
    private List<DbEntity> entities;

    EntityScanner(String basePackageName) throws Exception {
        entities = findEntities(basePackageName);
    }

    void printEntities() {
        entities.forEach(System.out::println);
    }

    private List<DbEntity> findEntities(String basePackageName)
            throws Exception {
        ClassPathScanningCandidateComponentProvider entityProvider
                = new ClassPathScanningCandidateComponentProvider(false);
        entityProvider.addIncludeFilter(new AbstractClassTestingTypeFilter() {
            @Override
            protected boolean match(ClassMetadata metadata) {
                return Arrays.asList(metadata.getInterfaceNames())
                        .contains(DbEntity.class.getName());
            }
        });
        return entityProvider.findCandidateComponents(basePackageName).stream()
                .map(BeanDefinition::getBeanClassName)
                .map(this::createInstance)
                .collect(toList());
    }

    // TODO: replace this with something proper
    @SneakyThrows
    private DbEntity createInstance(String className) {
        return (DbEntity) Class.forName(className).newInstance();
    }

}
