package sec.cyberprojectone.db;

import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.List;

import static java.util.stream.Collectors.toList;

class EntityScanner {
    public static List<Entity> findEntities(String basePackageName)
            throws Exception {

        ClassPathScanningCandidateComponentProvider entityProvider
                = new ClassPathScanningCandidateComponentProvider(false);

        entityProvider.addIncludeFilter(
                new AnnotationTypeFilter(DbEntity.class)
        );

        return entityProvider.findCandidateComponents(basePackageName).stream()
                .map(BeanDefinition::getBeanClassName)
                .map(EntityScanner::createInstance)
                .collect(toList());
    }

    // TODO: replace this with something proper
    @SneakyThrows
    private static Entity createInstance(String className) {
        return (Entity) Class.forName(className).newInstance();
    }

}
