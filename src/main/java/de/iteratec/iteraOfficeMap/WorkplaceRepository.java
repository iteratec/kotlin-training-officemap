package de.iteratec.iteraOfficeMap;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkplaceRepository extends JpaRepository<Workplace, Long> {

    Workplace findByXEqualsAndYEqualsOrNameEquals(int x, int y, String name);

}
