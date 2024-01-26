package vttp2023.batch4.paf.assessment.repositories;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {
	
	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;
	

	/*
	db.listings.aggregate([
    {
        $match: {
            "address.suburb": {
                $nin: ["", null]
            }
        }
    },
    {
        $project: {
            _id: "$address.suburb"
        }
    }
])
	 */
	public List<String> getSuburbs(String country) {
		MatchOperation matchOperation = Aggregation.match(Criteria.where("address.suburb").nin("", null));
		ProjectionOperation projectionOperation = Aggregation.project("_id", "address.suburb");
		Aggregation pipeline = Aggregation.newAggregation(matchOperation, projectionOperation);
		AggregationResults<String> results = template.aggregate(pipeline, "listings", String.class);
	
		return results.getMappedResults();
	}

	/*
	 db.listings.aggregate([
    {
        $match: {
            "address.suburb": { $regex: suburb, $options: "i" },
            "accommodates": { $gte: persons },
            "min_nights": { $lte: duration },
            "max_nights": { $gte: duration },
            "price": { $lte: priceRange, $gte: priceRange }
        }
    },
    {
        $project: {
            "_id": 1,
            "name": "$name",
            "accommodates": "$accommodates",
            "price": "$price"
        }
    },
    {
        $sort: { "price": -1 }
    }
])
	 */

	 
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		// Document doc = null;
		// doc.get(”price”, Number.class).floatValue()
		MatchOperation matchOperation = Aggregation.match(Criteria.where
														("address.suburb").regex(Pattern.quote(suburb), "i")
														.and("accommodates").gte(persons)
														.and("min_nights").lte(duration)
														.and("max_nights").gte(duration)
														.and("price").lte(priceRange).gte(priceRange));
		ProjectionOperation projectionOperation = Aggregation.project("_id")
														.and("$name").as("name")
														.and("$accommodates").as("accommodates")		
														.and("$price").as("price");											
		SortOperation sortOperation = Aggregation.sort(Sort.by(Direction.DESC, "price"));
		
		Aggregation pipeline = Aggregation.newAggregation(matchOperation, projectionOperation, sortOperation);
		AggregationResults<AccommodationSummary> results = template.aggregate(pipeline, "listings", AccommodationSummary.class);

		return results.getMappedResults();
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}
