package coms309.repository;

import coms309.entity.Group;
import coms309.entity.ImageGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageGalleryRepository extends JpaRepository<ImageGallery, Integer> {
}
