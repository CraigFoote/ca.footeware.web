
package ca.footeware.web.repositories;

import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import ca.footeware.web.models.Joke;

/**
 * @author Footeware.ca
 *
 */
public interface JokeRepository extends MongoRepository<Joke, String> {

	/**
	 * Get a joke by its ID.
	 * 
	 * @param id {@link String}
	 * @return {@link Joke}
	 */
	public Joke getById(String id);

	/**
	 * Get the set of jokes with provided title.
	 * 
	 * @param title {@link String}
	 * @return {@link Joke}
	 */
	public Set<Joke> getByTitle(String title);

}