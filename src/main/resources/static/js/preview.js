let isPrivate = false;

function goBack() {
    window.history.back();
}

function publishPost() {
    const urlParams = new URLSearchParams(window.location.search);
    const postId = urlParams.get('postId');
    fetch(`/posts/publish/${postId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ isPrivate: isPrivate, published: !isPrivate })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                window.location.href = `/posts/view?id=${postId}`;
            } else {
                console.error('Error publishing post:', data.message);
            }
        })
        .catch(error => console.error('Error:', error));
}

function previewThumbnail(event) {
    const reader = new FileReader();
    reader.onload = function () {
        const output = document.getElementById('thumbnail-preview');
        output.src = reader.result;
        output.style.display = 'block';
    };
    reader.readAsDataURL(event.target.files[0]);

    // 파일 업로드
    const formData = new FormData();
    formData.append('thumbnail', event.target.files[0]);

    const urlParams = new URLSearchParams(window.location.search);
    const postId = urlParams.get('postId');

    fetch(`/posts/upload-thumbnail/${postId}`, {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            if (data.imageUrl) {
                document.getElementById('thumbnail-preview').src = data.imageUrl;
            } else {
                console.error('Error uploading thumbnail:', data.error);
            }
        })
        .catch(error => console.error('Error:', error));
}

function setVisibility(visibility) {
    isPrivate = visibility === 'private';
    document.getElementById('publicBtn').classList.toggle('active', visibility === 'public');
    document.getElementById('privateBtn').classList.toggle('active', visibility === 'private');
}

document.addEventListener('DOMContentLoaded', (event) => {
    const urlParams = new URLSearchParams(window.location.search);
    const title = urlParams.get('title');
    if (title) {
        const titleElement = document.getElementById('postTitle');
        titleElement.innerText = title;
    }
});
