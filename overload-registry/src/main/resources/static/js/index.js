document.addEventListener("DOMContentLoaded", () => {
    loadMore();
});

window.addEventListener("scroll", () => {
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 200) {
        loadMore();
    }
});

let page = 0;
let isLoading = false;
let hasNext = true;


async function loadMore() {
    if (isLoading || !hasNext) return;

    isLoading = true;

    const res = await fetch(`/api/loads?page=${page}&size=10`);
    const data = await res.json();

    render(data.content);

    hasNext = !data.last;
    page++;

    isLoading = false;
}

function render(loads) {
    const container = document.getElementById("load-list");

    loads.forEach(load => {
        const el = document.createElement("a");

        el.href = `/loads/${load.id}`;
        el.className = "bg-white rounded-xl p-6 hover:shadow-lg transition-all duration-300 hover:-translate-y-1 cursor-pointer group";

        el.innerHTML = `
            <div class="flex flex-col h-full">

                <span class="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold w-fit mb-4 ${load.statusClass}">
                    ${load.statusText}
                </span>

                <h3 class="text-xl font-bold text-[#1A1D2E] mb-3 line-clamp-2 group-hover:text-[#6B46C1] transition-colors">
                    ${load.loadName}
                </h3>

                <div class="flex items-center justify-between pt-4 border-t border-gray-100">
                    <div class="flex items-center gap-3">
                        <img class="w-8 h-8 rounded-full object-cover" src="/images/search-image">
                        <div class="flex flex-col">
                            <span class="text-sm font-medium text-gray-900">익명</span>
                            <span class="text-xs text-gray-500">
                                ${formatDate(load.createAt)}
                            </span>
                        </div>
                    </div>

                    <div class="flex items-center gap-4 text-gray-500 text-sm">
                        <div class="flex items-center gap-1">
                            <i class="ri-eye-line"></i><span>0</span>
                        </div>
                        <div class="flex items-center gap-1">
                            <i class="ri-chat-3-line"></i><span>0</span>
                        </div>
                    </div>
                </div>

            </div>
        `;

        container.appendChild(el);
    });
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}
