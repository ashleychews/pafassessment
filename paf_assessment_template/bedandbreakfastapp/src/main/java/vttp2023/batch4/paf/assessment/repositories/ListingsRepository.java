package vttp2023.batch4.paf.assessment.repositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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
	 *
	 */



	public List<String> getSuburbs(String country) {
		Query query = Query.query(Criteria.where("address.country").is(country));
		
		List<String> suburbs = template.find(query, String.class, "listings");
		System.out.println("suburbs" + suburbs);
		return suburbs;
	}

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 * 
	 * db.listings.find(
    	{
    	"address.suburb": {$regex: "Darlinghurst", $options:"i"},
    	accommodates: 2,
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
			.and("accommodates").is(persons)
			.and("duration").is(duration)
			.and("price").lte(priceRange)
		);

		List<Document> doc = template.find(query, Document.class, "listings");
		//convert document to acc summary
		List<AccommodationSummary> accSum = new LinkedList<>();

		for (Document d: doc) {
			AccommodationSummary list = new AccommodationSummary();

			Document address = d.get("address", Document.class);
			
			list.setId(d.getString("_id"));
			list.setName(d.getString("name"));
			list.setPrice(d.getDouble("price"));
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
