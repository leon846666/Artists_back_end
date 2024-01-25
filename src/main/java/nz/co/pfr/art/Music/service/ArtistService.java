package nz.co.pfr.art.Music.service;

import nz.co.pfr.art.Music.entities.Artist;
import nz.co.pfr.art.Music.entities.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArtistService {
    Logger log = LoggerFactory.getLogger(ArtistService.class);
    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<String> getMostProductiveArtists(Integer topn) {
        var artists = artistRepository.findAll();

        //if the total artist size is smaller than the topn number,  use the artist size  as the topn number to prevent indexOutOfRange Exception
        topn = topn>artists.size()? artists.size() : topn;

        // extract a method to deal with the artists and their cd tracks count
        HashMap<String, Integer> artistTrackCountMap = getArtistCDTracksCountMap(artists);


          // solution 1:

        // Sorting the map by trackCount so the less tracks artist will be at the top of the map
        Map<String, Integer> artistTrackCountMapSorted = artistTrackCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        int highest = 0;
        var mostProductiveArtists = new ArrayList<String>();

        for (var artist : artistTrackCountMapSorted.keySet()) {
            var totalTrackCount = artistTrackCountMap.get(artist);

            if (highest < totalTrackCount) {
                highest = totalTrackCount;
                // put the higher track count in the front of the lower track count
                mostProductiveArtists.add(0,artist);
            } else if (highest == totalTrackCount) {
                mostProductiveArtists.add(0,artist);
            }
            log.info("artist: {} total tracks: {}", artist, artistTrackCountMap.get(artist));
        }
        log.info("most productive artist is: {}", mostProductiveArtists);
        return mostProductiveArtists.subList(0, topn);


        // solution 2
        // Sorting the map by trackCount so the most productive tracks artist will be at the top of the map
        // this solution is an optimized version of my solution 1, just an optimized for my solution ,
        // since my solution 1 is using sort to make sure there is an order in the map
        // so I just sort the map by the number of track.
       /* Map<String, Integer> artistTrackCountMapSorted = artistTrackCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        // and use the stream api convert the map to a list that contains a sorted from maximum track numbers to lower track numbers list
        List<String> listArtists = artistTrackCountMapSorted.keySet().stream().toList();

        return listArtists.subList(0,topn);*/
    }

    public List<String> getMostProductiveSQLArtists(Integer topn) {

        var artists = artistRepository.findAllArtists();

        //if the total artist size is smaller than the topn number,  use the artist size  as the topn number to prevent indexOutOfRange Exception
        topn = topn>artists.size()? artists.size() : topn;

        HashMap<String, Integer> artistTrackCountMap = getArtistCDTracksCountMap(artists);

        Map<String, Integer> artistTrackCountMapSorted = artistTrackCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        // and use the stream api convert the map to a list that contains a sorted from maximum track numbers to lower track numbers list
        List<String> listArtists = artistTrackCountMapSorted.keySet().stream().toList();

        return listArtists.subList(0,topn);

    }


    // an extracted method from original code since it is used in multiple places
    private HashMap<String, Integer> getArtistCDTracksCountMap(List<Artist> artists) {
        var artistTrackCountMap = new HashMap<String, Integer>();
        for (int i = 0; i < artists.size(); i++) {
            var artist = artists.get(i);
            log.info("artist name {} cds {}", artist.getName(), artist.getCds().size());

            int trackCount = 0;
            for (int j = 0; j < artist.getCds().size(); j++) {
                var cd = artist.getCds().get(j);
                log.info("cd title {} tracks {}", cd.getTitle(), cd.getTracks().size());
                trackCount += cd.getTracks().size();

            }
            artistTrackCountMap.put(artist.getName(), trackCount);

        }
        return artistTrackCountMap;
    }
}
