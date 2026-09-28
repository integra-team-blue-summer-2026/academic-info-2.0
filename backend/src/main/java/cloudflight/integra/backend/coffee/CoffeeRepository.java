package cloudflight.integra.backend.coffee;

import cloudflight.integra.backend.coffee.model.Coffee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
}
