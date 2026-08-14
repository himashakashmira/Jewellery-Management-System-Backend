package lk.ijse.jewellery_management_system.repository;

import lk.ijse.jewellery_management_system.entity.GoldRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoldRateRepository extends JpaRepository<GoldRate, Integer> {
    // අලුත්ම රන් මිල මුලින්ම එන විදිහට ගන්න පස්සේ මෙතනට query එකක් ලියමු
}
