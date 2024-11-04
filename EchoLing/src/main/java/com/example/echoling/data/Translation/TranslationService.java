package com.example.echoling.data.Translation;

import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TranslationService {

    @Autowired
    private TranslationRepository translationRepository;

    public void saveTranslation(Translation translation) {
        translationRepository.save(translation);
    }

    public Optional<Translation> findById(Long id) {
        return translationRepository.findById(id);
    }
    public List<Translation> findTranslationsForUser(
            List<String> nativeLanguages,
            List<String> learningLanguages,
            String search,
            String difficulty,
            String sourceLang,
            String targetLang,
            Integer minViews,
            LocalDate minDate,
            LocalDate maxDate,
            String sort,
            int page, int size) {

        LocalDateTime minDateTime = minDate != null ? minDate.atStartOfDay() : null;
        LocalDateTime maxDateTime = maxDate != null ? maxDate.atTime(23, 59, 59) : null;

        Specification<Translation> spec = buildSpecification(
                nativeLanguages, learningLanguages, search, difficulty,
                sourceLang, targetLang, minViews, minDateTime, maxDateTime);

        Pageable pageable = PageRequest.of(page, size, getSort(sort));
        System.out.println();
        Page<Translation> translationPage = translationRepository.findAll(spec, pageable);

        List<Translation> content = translationPage.getContent();
        return content;
    }

    private Specification<Translation> buildSpecification(
            List<String> nativeLanguages,
            List<String> learningLanguages,
            String search,
            String difficulty,
            String sourceLang,
            String targetLang,
            Integer minViews,
            LocalDateTime minDate,
            LocalDateTime maxDate) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!nativeLanguages.isEmpty()) {
                if(!learningLanguages.isEmpty())
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.and(
                                    root.get("sourceLang").in(nativeLanguages),
                                    root.get("targetLang").in(learningLanguages)
                            ),
                            criteriaBuilder.and(
                                    root.get("sourceLang").in(learningLanguages),
                                    root.get("targetLang").in(nativeLanguages)
                            )
                    ));
                else
                    predicates.add(criteriaBuilder.or(
                                    root.get("sourceLang").in(nativeLanguages),
                                    root.get("targetLang").in(nativeLanguages)));
            }
            else if (!learningLanguages.isEmpty())
                predicates.add(criteriaBuilder.or(
                        root.get("sourceLang").in(learningLanguages),
                        root.get("targetLang").in(learningLanguages)));

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern);
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern);
                predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate));
            }

            if (difficulty != null && !difficulty.trim().isEmpty())
                predicates.add(criteriaBuilder.equal(root.get("difficulty"), difficulty));

            if (sourceLang != null && !sourceLang.trim().isEmpty())
                predicates.add(criteriaBuilder.equal(root.get("sourceLang"), sourceLang));

            if (targetLang != null && !targetLang.trim().isEmpty())
                predicates.add(criteriaBuilder.equal(root.get("targetLang"), targetLang));

            if (minViews != null)
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("views"), minViews));

            if (minDate != null)
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("creationTime"), minDate));

            if (maxDate != null)
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("creationTime"), maxDate));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Sort getSort(String sort) {
        if (sort != null && !sort.trim().isEmpty()) {
            switch (sort) {
                case "popularity":
                    return Sort.by(Sort.Direction.DESC, "views");
                case "oldest":
                    return Sort.by(Sort.Direction.ASC, "creationTime");
                case "newest":
                    return Sort.by(Sort.Direction.DESC, "creationTime");
                default:
                    return Sort.unsorted();
            }
        } else {
            return Sort.unsorted();
        }
    }
    public void deleteTranslation(Translation translation) {
        translationRepository.delete(translation);
    }


    public List<Translation> findAllById(List<Long> translationIds) {
        return translationRepository.findAllById(translationIds);
    }

}
