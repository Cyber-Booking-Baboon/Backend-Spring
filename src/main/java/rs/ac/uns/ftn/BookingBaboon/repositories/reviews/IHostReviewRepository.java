package rs.ac.uns.ftn.BookingBaboon.repositories.reviews;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.BookingBaboon.domain.reviews.HostReview;

@Repository
public interface IHostReviewRepository extends JpaRepository<HostReview, Long> {
}
