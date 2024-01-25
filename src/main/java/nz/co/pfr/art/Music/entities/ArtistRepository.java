package nz.co.pfr.art.Music.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {

    @Query(value = "SELECT * FROM artist", nativeQuery = true)
    List<Artist> findAllArtists();

}
