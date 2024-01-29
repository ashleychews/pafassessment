package vttp2023.batch4.paf.assessment.repositories;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 *
	 *db.listings.distinct(
    	address.suburb"
	);

	edited after submission
	 db.listings.aggregate([
			{
				$match: {
					"address.suburb": { $exists: true },
					"address.suburb": { $ne: "" },
				},
			},
			{
				$project: {
					_id: "$address.suburb",
				},
			},
			{
				$sort: {
					_id: 1,
				},
			},
		])
	 */

	public List<String> getSuburbs(String country) {
		//Query query = Query.query(Criteria.where("address.country").is(country));
		
		//List<String> suburbs = template.findDistinct(new Query(), "address.suburb", "listings", String.class);
		//System.out.println("suburbs" + suburbs);
		//return suburbs;
		MatchOperation matchStage = Aggregation.match(Criteria.where("address.suburb").ne("").exists(true));
		ProjectionOperation projectStage = Aggregation
			.project()
      		.and("address.suburb")
      		.as("_id");

    	SortOperation sortStage = Aggregation.sort(Sort.Direction.ASC, "_id");

    	Aggregation pipeline = Aggregation.newAggregation(matchStage,projectStage,sortStage);

    	AggregationResults<Document> results = template.aggregate(pipeline,"listings",Document.class);

		List<String> suburbs = new ArrayList<>();
    	for (Document document : results) {
      		String suburb = document.getString("_id");
      		if (!suburbs.contains(suburb)) suburbs.add(suburb);
    		}
    	return suburbs;
	}

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 * 
	db.listings.find(
		{
		"address.suburb": {$regex: "Darlinghurst", $options:"i"},
		accommodates: {$lte:2},
		min_nights: {$gte:2},
		price: {$lte: 100}
		},
		{
			_id:1,
			name:1,
			accommodates:1,
			price:1
		}
		
		).sort(
			{"price":-1}
	);
	 *
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		Query query = Query.query(Criteria.where("address.suburb").is(suburb)
			.and("accommodates").lte(persons)
			.and("min_nights").lte(duration)
			.and("price").gte(priceRange)
		);

		List<Document> doc = template.find(query, Document.class, "listings");
		System.out.println("doc" + doc);

		//convert document to acc summary
		List<AccommodationSummary> accSum = new LinkedList<>();

		for (Document d: doc) {
			AccommodationSummary list = new AccommodationSummary();
			
			list.setId(d.getString("_id"));
			list.setName(d.getString("name"));
			list.setPrice(d.get("price", Number.class).floatValue());
			list.setAccomodates(d.getInteger("accommodates"));

			accSum.add(list);
		}
		System.out.println("acc sumary" + accSum);
		return accSum;

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
