package cloudflight.integra.backend.coffeemug;

import cloudflight.integra.backend.coffeemug.model.CoffeeMug;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface CoffeeMugRepository extends JpaRepository<CoffeeMug, Long> {
}
