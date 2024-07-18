package org.example.blogproject.post.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public Tag findOrCreateTag(String tagName) {
        Optional<Tag> optionalTag = tagRepository.findByTagName(tagName);
        if (optionalTag.isPresent()) {
            return optionalTag.get();
        } else {
            Tag newTag = new Tag();
            newTag.setTagName(tagName);
            return tagRepository.save(newTag);
        }
    }
}
