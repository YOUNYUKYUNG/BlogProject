package org.example.blogproject.post.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag findOrCreateTag(String tagName) {
        return tagRepository.findByTagName(tagName).orElseGet(() -> {
            Tag newTag = new Tag();
            newTag.setTagName(tagName);
            return tagRepository.save(newTag);
        });
    }

    public Set<Tag> findAllTags() {
        return Set.copyOf(tagRepository.findAll());
    }

    public Optional<Tag> findTagById(Long tagId) {
        return tagRepository.findById(tagId);
    }

    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public void deleteTag(Long tagId) {
        tagRepository.deleteById(tagId);
    }
}
