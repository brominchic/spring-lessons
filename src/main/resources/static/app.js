const apiBase = './api/farmer';
const appliedActionsStorageKey = 'farmerCalendarAppliedActions';
const promoCodesStorageKey = 'farmerCalendarPromoCodes';
const discountPercentsStorageKey = 'farmerCalendarDiscountPercents';
const campaignActualsStorageKey = 'farmerCalendarActuals';
const calendarHistoryStorageKey = 'farmerCalendarHistory';
const generatedSocialTextsStorageKey = 'farmerCalendarGeneratedSocialTexts';
const onboardingStorageKey = 'farmerCalendarOnboardingSeenV4';
const assortmentBatchSize = 32;

const state = {
    farmers: [],
    assortment: [],
    calendar: null,
    adminEvents: [],
    calendarLoaded: false,
    selectedFarmerId: '',
    editingEventId: '',
    periodMode: 'month',
    selectedYear: defaultYear(),
    selectedMonth: defaultMonthNumber(),
    selectedQuarter: quarterFromMonth(defaultMonthNumber()),
    appliedActions: loadAppliedActions(),
    promoCodes: loadPromoCodes(),
    discountPercents: loadDiscountPercents(),
    actuals: loadCampaignActuals(),
    history: loadCalendarHistory(),
    generatedSocialTexts: loadGeneratedSocialTexts(),
    createdProductIds: new Set(),
    calculator: {
        eventKey: '',
        discountPercent: 10,
        stock: 50,
        price: 1000,
        marginPercent: 30
    },
    onboardingStep: 0
};

const marketplaceChannelActions = {
    'Витрина': {
        label: 'Витрина продаж',
        description: 'Идея для карточки товара: обновить название, описание, фото, скидку или промокод.',
        doneLabel: 'Витрина обновлена'
    },
    'Чат с покупателем': {
        label: 'Чат фермера с покупателем',
        description: 'Идея сообщения: коротко связать товар, повод и выгоду для покупателя.',
        doneLabel: 'Сообщение предложено'
    },
    'Блог': {
        label: 'Блог',
        description: 'Идея для блога: подготовить тему или статью от фермера под событие и товар.',
        doneLabel: 'Пост предложен'
    },
    'Рецепты': {
        label: 'Рецепты',
        description: 'Идея рецепта: показать блюдо, где товар выступает главным ингредиентом.',
        doneLabel: 'Рецепт предложен'
    },
    'Сторисы': {
        label: 'Сторисы с фото и текстом',
        description: 'Идея сторис: собрать короткую серию с фото, выгодой и промокодом.',
        doneLabel: 'Сторис предложен'
    },
    'E-mail подборка': {
        label: 'Ежемесячная e-mail рассылка',
        description: 'Идея рассылки: включить товар в тематическую подборку рекомендаций.',
        doneLabel: 'E-mail предложен'
    },
    'Пуш': {
        label: 'Пуш-уведомления',
        description: 'Идея пуша: коротко показать повод, товар и выгоду.',
        doneLabel: 'Пуш предложен'
    },
    'Соцсети': {
        label: 'Соцсети и мессенджеры',
        description: 'Идея поста: подготовить текст для соцсетей или мессенджеров.',
        doneLabel: 'Публикация предложена'
    }
};

const marketplaceChannels = new Set(Object.keys(marketplaceChannelActions));

const deliveryPromotionPresets = [
    {
        title: 'Фреш-неделя: овощи, фрукты и зелень',
        eventType: 'Сезонность',
        durationDays: 7,
        roiPercent: 136,
        tags: ['Овощи и фрукты', 'натуральное', 'локальный продукт'],
        channels: ['Витрина', 'Пуш', 'Сторисы'],
        boosts: [
            {tag: 'Овощи и фрукты', multiplier: 1.35, rationale: 'Типовая недельная подборка свежих категорий в сервисах доставки продуктов.', sourceTitle: 'Паттерн grocery delivery', sourceUrl: ''},
            {tag: 'Сыры', multiplier: 1.12, rationale: 'Сыры работают как дополняющая категория к овощам, фруктам и готовым корзинам.', sourceTitle: 'Паттерн grocery delivery', sourceUrl: ''}
        ]
    },
    {
        title: 'Молочная корзина на неделю',
        eventType: 'Тренд',
        durationDays: 5,
        roiPercent: 128,
        tags: ['Яйца и молочные продукты', 'завтрак', 'семья'],
        channels: ['Витрина', 'Пуш', 'E-mail подборка'],
        boosts: [
            {tag: 'Яйца и молочные продукты', multiplier: 1.22, rationale: 'Регулярная закупка молока, яиц, творога и йогуртов хорошо подходит для коротких промо-периодов.', sourceTitle: 'Паттерн grocery delivery', sourceUrl: ''}
        ]
    },
    {
        title: 'Готовим ужин быстро',
        eventType: 'Тренд',
        durationDays: 10,
        roiPercent: 132,
        tags: ['ужин', 'белок', 'заморозка', 'гастрономия'],
        channels: ['Рецепты', 'Витрина', 'Пуш'],
        boosts: [
            {tag: 'Мясо и птица', multiplier: 1.28, rationale: 'Сценарий быстрого ужина поднимает спрос на белковые продукты и полуфабрикаты.', sourceTitle: 'Паттерн grocery delivery', sourceUrl: ''},
            {tag: 'Заморозка', multiplier: 1.24, rationale: 'Замороженные и готовые продукты покупают как решение для быстрой готовки.', sourceTitle: 'Паттерн grocery delivery', sourceUrl: ''}
        ]
    },
    {
        title: 'Бесплатная доставка от суммы заказа',
        eventType: 'Тренд',
        durationDays: 14,
        roiPercent: 118,
        tags: ['корзина', 'повторный заказ', 'бакалея'],
        channels: ['Витрина', 'Пуш', 'Чат с покупателем'],
        boosts: [
            {tag: 'Бакалея', multiplier: 1.18, rationale: 'Порог бесплатной доставки стимулирует добирать товары длительного хранения.', sourceTitle: 'Паттерн grocery delivery', sourceUrl: ''},
            {tag: 'Напитки', multiplier: 1.12, rationale: 'Напитки часто добирают до минимальной суммы заказа.', sourceTitle: 'Паттерн grocery delivery', sourceUrl: ''}
        ]
    },
    {
        title: 'Скидка на первый или повторный заказ',
        eventType: 'Тренд',
        durationDays: 14,
        roiPercent: 125,
        tags: ['первый заказ', 'повторный заказ', 'промокод'],
        channels: ['Пуш', 'E-mail подборка', 'Сторисы'],
        boosts: [
            {tag: 'подарок', multiplier: 1.20, rationale: 'Промокод на первый или повторный заказ помогает вернуть пользователя в сервис.', sourceTitle: 'Паттерн grocery delivery', sourceUrl: ''},
            {tag: 'Бакалея', multiplier: 1.12, rationale: 'Для промокода подходят универсальные категории с понятной регулярной покупкой.', sourceTitle: 'Паттерн grocery delivery', sourceUrl: ''}
        ]
    }
];

const onboardingSteps = [
    {
        icon: '◷',
        title: 'Настройте календарь',
        text: 'Выберите месяц или квартал. Если нужно сузить выдачу, используйте фильтр категории.',
        target: '.calendar-controls'
    },
    {
        icon: '↗',
        title: 'Проверьте прогноз',
        text: 'В карточке события видно выбранный товар, ожидаемые заказы, рост и ROI. Так понятно, зачем запускать акцию.',
        target: '.materials-section'
    },
    {
        icon: '▦',
        title: 'Оцените план',
        text: 'Блок планирования собирает приоритеты, калькулятор, рейтинг товаров и сезонность в один рабочий план.',
        target: '.planning-tools'
    },
    {
        icon: '±',
        title: 'Калькулятор акций',
        text: 'В калькуляторе можно вручную менять скидку, остаток, цену и маржу, чтобы заранее увидеть заказы, выручку, затраты и ROI.',
        target: '.calculator-card'
    },
    {
        icon: '★',
        title: 'Выберите товары',
        text: 'Рейтинг товаров подсказывает SKU с лучшим потенциалом, чтобы фермер не тратил акцию на слабую позицию.',
        target: '.ranking-card'
    },
    {
        icon: '☼',
        title: 'Смотрите сезон',
        text: 'Сезонная карта показывает месяцы с усиленным спросом и помогает заранее готовить акции под пики продаж.',
        target: '.seasonal-card'
    },
    {
        icon: '%',
        title: 'Примените совет',
        text: 'Нажмите кнопку под оффером или действием на витрине. Совет сохранится как выполненный прямо в демо-кабинете.',
        target: '.advice-controls'
    }
];

const categoryImages = {
    'Мясо и птица': 'https://images.unsplash.com/photo-1603048297172-c92544798d5a?auto=format&fit=crop&w=520&q=70',
    'Овощи и фрукты': 'https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=520&q=70',
    'Сыры': 'https://images.unsplash.com/photo-1452195100486-9cc805987862?auto=format&fit=crop&w=520&q=70',
    'Яйца и молочные продукты': 'https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=520&q=70',
    'Рыба и морепродукты': 'https://images.unsplash.com/photo-1510130387422-82bed34b37e9?auto=format&fit=crop&w=520&q=70',
    'Мед и пчеловодство': 'https://images.unsplash.com/photo-1587049352851-8d4e89133924?auto=format&fit=crop&w=520&q=70',
    'Сладости': 'https://images.unsplash.com/photo-1481391319762-47dff72954d9?auto=format&fit=crop&w=520&q=70',
    'Бакалея': 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=520&q=70',
    'Хлеб и выпечка': 'https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=520&q=70',
    'Заморозка': 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=520&q=70',
    'Напитки': 'https://images.unsplash.com/photo-1544145945-f90425340c7e?auto=format&fit=crop&w=520&q=70'
};

document.addEventListener('DOMContentLoaded', init);

function init() {
    resetPeriodControls();
    bindNavigation();
    bindFarmerPicker();
    bindCalendarControls();
    bindCustomProductForm();
    bindAdminControls();
    bindAdviceActions();
    syncTrainingInviteState();
    window.addEventListener('hashchange', showViewByHash);
    window.addEventListener('resize', updateTourPosition);
    window.addEventListener('scroll', updateTourPosition, true);
    window.addEventListener('wheel', preventOnboardingScroll, {passive: false});
    window.addEventListener('touchmove', preventOnboardingScroll, {passive: false});
    window.addEventListener('keydown', preventOnboardingKeyScroll, true);
    showView(window.location.hash.replace('#', '') || 'calendar');
}

function bindCustomProductForm() {
    renderCustomProductCategoryOptions();
    document.getElementById('custom-product-reset').addEventListener('click', resetCustomProductForm);
    document.getElementById('custom-product-form').addEventListener('submit', addCustomProduct);
}

function bindNavigation() {
    document.querySelector('.brand').addEventListener('click', (event) => {
        event.preventDefault();
        showView('calendar');
    });
    document.querySelectorAll('[data-view]').forEach((button) => {
        button.addEventListener('click', () => {
            setActiveMenuItem(button.dataset.view);
            button.classList.remove('menu-item-flash');
            void button.offsetWidth;
            button.classList.add('menu-item-flash');
            showView(button.dataset.view);
        });
    });
}

function bindFarmerPicker() {
    document.getElementById('farmer-avatar').addEventListener('click', (event) => {
        event.stopPropagation();
        toggleFarmerMenu();
    });
    document.addEventListener('click', (event) => {
        if (!document.getElementById('farmer-switcher').contains(event.target)) {
            closeFarmerMenu();
        }
    });
}

function bindCalendarControls() {
    document.getElementById('refresh-calendar').addEventListener('click', loadCalendar);
    document.getElementById('training-invite').addEventListener('click', openOnboarding);
    document.getElementById('training-help').addEventListener('click', showTrainingInvite);
    document.querySelectorAll('[data-start-onboarding]').forEach((button) => {
        button.addEventListener('click', (event) => {
            event.preventDefault();
            openOnboarding();
        });
    });
    document.querySelectorAll('[data-period-mode]').forEach((button) => {
        button.addEventListener('click', () => {
            state.periodMode = button.dataset.periodMode;
            updatePeriodControls();
            loadCalendar();
        });
    });
    document.querySelectorAll('[data-period-month]').forEach((button) => {
        button.addEventListener('click', () => {
            state.selectedMonth = Number(button.dataset.periodMonth);
            updatePeriodControls();
            loadCalendar();
        });
    });
    document.querySelectorAll('[data-period-quarter]').forEach((button) => {
        button.addEventListener('click', () => {
            state.selectedQuarter = Number(button.dataset.periodQuarter);
            updatePeriodControls();
            loadCalendar();
        });
    });
    document.querySelectorAll('[data-year-step]').forEach((button) => {
        button.addEventListener('click', () => {
            state.selectedYear += Number(button.dataset.yearStep);
            updatePeriodControls();
            loadCalendar();
        });
    });
    document.getElementById('category').addEventListener('change', loadCalendar);
}

function updatePeriodControls() {
    document.getElementById('period-year').textContent = String(state.selectedYear);
    document.querySelectorAll('[data-period-mode]').forEach((button) => {
        button.classList.toggle('active', button.dataset.periodMode === state.periodMode);
    });
    document.getElementById('month-options').hidden = state.periodMode !== 'month';
    document.getElementById('quarter-options').hidden = state.periodMode !== 'quarter';
    document.querySelectorAll('[data-period-month]').forEach((button) => {
        button.classList.toggle('active', Number(button.dataset.periodMonth) === state.selectedMonth);
    });
    document.querySelectorAll('[data-period-quarter]').forEach((button) => {
        button.classList.toggle('active', Number(button.dataset.periodQuarter) === state.selectedQuarter);
    });
}

function bindAdminControls() {
    document.getElementById('admin-event-form').addEventListener('submit', saveAdminEvent);
    document.getElementById('admin-new-event').addEventListener('click', () => renderAdminForm());
    document.getElementById('admin-reset').addEventListener('click', () => renderAdminForm());
    document.getElementById('admin-add-boost').addEventListener('click', () => addAdminBoostRow());
    renderAdminPresets();
}

function resetPeriodControls() {
    state.periodMode = 'month';
    state.selectedYear = defaultYear();
    state.selectedMonth = defaultMonthNumber();
    state.selectedQuarter = quarterFromMonth(state.selectedMonth);
    updatePeriodControls();
}

function bindAdviceActions() {
    document.addEventListener('click', (event) => {
        const actualButton = event.target.closest('[data-save-actual]');
        if (actualButton) {
            saveActuals(actualButton);
            return;
        }

        const generateSocialButton = event.target.closest('[data-generate-social-text]');
        if (generateSocialButton) {
            generateSocialText(generateSocialButton);
            return;
        }

        const aiIdeaButton = event.target.closest('[data-generate-ai-idea]');
        if (aiIdeaButton) {
            generateAiIdea(aiIdeaButton);
            return;
        }

        const metricsButton = event.target.closest('[data-analyze-metrics]');
        if (metricsButton) {
            analyzeMetrics(metricsButton);
            return;
        }

        if (event.target.closest('[data-export-plan]')) {
            exportCampaignPlanCsv();
            return;
        }

        if (event.target.closest('[data-close-onboarding]')) {
            closeOnboarding();
            return;
        }

        if (event.target.closest('[data-onboarding-next]')) {
            nextOnboardingStep();
            return;
        }

        if (event.target.closest('[data-onboarding-prev]')) {
            previousOnboardingStep();
            return;
        }

        const editButton = event.target.closest('[data-admin-edit]');
        if (editButton) {
            editAdminEvent(editButton.dataset.adminEdit);
            return;
        }

        const deleteButton = event.target.closest('[data-admin-delete]');
        if (deleteButton) {
            deleteAdminEvent(deleteButton.dataset.adminDelete);
            return;
        }

        if (event.target.closest('[data-remove-boost]')) {
            event.target.closest('.admin-boost-row').remove();
            return;
        }

        const presetButton = event.target.closest('[data-admin-preset]');
        if (presetButton) {
            applyAdminPreset(Number(presetButton.dataset.adminPreset));
        }
    });
    document.addEventListener('change', (event) => {
        const promoInput = event.target.closest('[data-promo-code-input]');
        if (promoInput) {
            if (savePromoCodeInput(promoInput) && state.calendar) {
                renderCalendar(state.calendar);
            }
            return;
        }

        const discountInput = event.target.closest('[data-promo-discount-input]');
        if (discountInput) {
            if (saveDiscountPercentInput(discountInput) && state.calendar) {
                renderCalendar(state.calendar);
            }
            return;
        }

        const calculatorField = event.target.closest('[data-calculator-field]');
        if (calculatorField) {
            updateCalculatorState();
            renderPlanningTools(state.calendar);
            return;
        }
    });
}

function showViewByHash() {
    showView(window.location.hash.replace('#', '') || 'calendar');
}

async function showView(view) {
    const selectedButton = document.querySelector(`[data-view="${view}"]`);
    const activeView = selectedButton == null ? 'calendar' : view;
    const isCalendar = activeView === 'calendar';
    const isAddProduct = activeView === 'add-product';
    const isAdmin = activeView === 'admin';
    const title = isCalendar ? 'Календарь продаж' : selectedButton.dataset.title;
    const subtitle = isCalendar
            ? 'Планируйте события, акции и публикации под реальные товары фермера'
            : isAddProduct
                    ? 'Создавайте товар в базе и сразу используйте его в календаре'
                    : 'Управляйте праздниками, трендами, локальными событиями и коэффициентами';

    document.getElementById('calendar-view').classList.toggle('active', isCalendar);
    document.getElementById('add-product-view').classList.toggle('active', isAddProduct);
    document.getElementById('admin-view').classList.toggle('active', isAdmin);
    setActiveMenuItem(activeView);

    document.getElementById('page-title').textContent = title;
    document.getElementById('page-subtitle').textContent = subtitle;
    window.history.replaceState(null, '', `#${activeView}`);

    if (isCalendar && !state.calendarLoaded) {
        state.calendarLoaded = true;
        await loadMarketingPage();
        showOnboardingIfNeeded();
    } else if (isAddProduct) {
        await ensureFarmersLoaded();
        renderCustomProductFarmerOptions();
        renderCustomProductCategoryOptions();
    } else if (isAdmin) {
        renderAdminForm();
        await loadAdminEvents();
    }
}

function setActiveMenuItem(activeView) {
    document.querySelectorAll('.menu-item[data-view]').forEach((button) => {
        const isActive = button.dataset.view === activeView;
        button.classList.toggle('active', isActive);
        button.setAttribute('aria-current', isActive ? 'page' : 'false');
    });
}

async function loadMarketingPage() {
    await loadFarmers();
    await loadCalendarState();
    await loadAssortment();
    await loadCalendar();
}

async function loadFarmers() {
    state.farmers = await fetchJson(`${apiBase}/farmers`);
    renderFarmerMenu();
    updateFarmerAvatar();
}

async function ensureFarmersLoaded() {
    if (!state.farmers.length) {
        await loadFarmers();
    }
}

function renderFarmerMenu() {
    const menu = document.getElementById('farmer-menu');
    menu.innerHTML = `
        <button class="farmer-option" type="button" data-farmer-id="">
            <span class="farmer-option-avatar">Все</span>
            <span><strong>Все фермеры</strong><small>общая витрина ассортимента</small></span>
        </button>
    ` + state.farmers.map((farmer) => `
        <button class="farmer-option" type="button" data-farmer-id="${farmer.organizationId}">
            <span class="farmer-option-avatar">${escapeHtml(initials(farmer.shopName))}</span>
            <span><strong>${escapeHtml(farmer.shopName)}</strong><small>${escapeHtml(farmer.region)}</small></span>
        </button>
    `).join('');
    menu.querySelectorAll('.farmer-option').forEach((button) => {
        button.addEventListener('click', () => selectFarmer(button.dataset.farmerId));
    });
}

async function selectFarmer(farmerId) {
    state.selectedFarmerId = farmerId;
    updateFarmerAvatar();
    closeFarmerMenu();
    await loadCalendarState();
    await loadAssortment();
    await loadCalendar();
}

function updateFarmerAvatar() {
    const farmer = selectedFarmer();
    const avatar = document.getElementById('farmer-avatar-initials');
    avatar.textContent = farmer ? initials(farmer.shopName) : 'Все';
    document.querySelectorAll('.farmer-option').forEach((button) => {
        button.classList.toggle('active', button.dataset.farmerId === state.selectedFarmerId);
    });
}

function selectedFarmer() {
    return state.farmers.find((farmer) => String(farmer.organizationId) === String(state.selectedFarmerId));
}

function selectedOrganizationId() {
    return state.selectedFarmerId ? Number(state.selectedFarmerId) : null;
}

async function loadCalendarState() {
    const params = new URLSearchParams();
    if (state.selectedFarmerId) {
        params.set('organizationId', state.selectedFarmerId);
    }
    const url = params.toString() ? `${apiBase}/state?${params.toString()}` : `${apiBase}/state`;
    try {
        applyCalendarState(await fetchJson(url));
    } catch (error) {
        console.warn('Calendar state was not loaded from DB', error);
    }
}

function applyCalendarState(payload) {
    state.appliedActions = payload?.appliedActions || {};
    state.promoCodes = payload?.promoCodes || {};
    state.discountPercents = payload?.discountPercents || {};
    state.actuals = payload?.actuals || {};
    state.history = payload?.history || [];
    saveAppliedActions();
    savePromoCodes();
    saveDiscountPercents();
    saveCampaignActuals();
    saveCalendarHistory();
}

function toggleFarmerMenu() {
    const menu = document.getElementById('farmer-menu');
    const isOpen = menu.classList.toggle('open');
    document.getElementById('farmer-avatar').setAttribute('aria-expanded', String(isOpen));
}

function closeFarmerMenu() {
    document.getElementById('farmer-menu').classList.remove('open');
    document.getElementById('farmer-avatar').setAttribute('aria-expanded', 'false');
}

async function loadAssortment() {
    const params = new URLSearchParams();
    if (state.selectedFarmerId) {
        params.set('organizationId', state.selectedFarmerId);
    }
    const url = params.toString() ? `${apiBase}/assortment?${params.toString()}` : `${apiBase}/assortment`;
    state.assortment = await fetchJson(url);
    renderAssortment(state.assortment);
}

async function loadCalendar() {
    const period = state.periodMode;
    const year = String(state.selectedYear);
    const month = period === 'quarter'
            ? String((state.selectedQuarter - 1) * 3 + 1)
            : String(state.selectedMonth);
    const category = document.getElementById('category').value;
    const params = new URLSearchParams({period, year, month});
    if (category) {
        params.set('category', category);
    }
    if (state.selectedFarmerId) {
        params.set('organizationId', state.selectedFarmerId);
    }

    state.calendar = await fetchJson(`${apiBase}/calendar?${params.toString()}`);
    fillCategoryFilter(state.calendar.categories);
    renderCalendar(state.calendar);
    renderAssortment(state.assortment);
}

function renderAssortment(items) {
    const grid = document.getElementById('product-grid');
    const eventByProduct = highlightedEventsByProduct();
    const selectedCategory = document.getElementById('category').value;
    const visibleItems = items.filter((item) => {
        const isHighlighted = eventByProduct.has(item.name);
        const isCreatedVisible = state.createdProductIds.has(String(item.productId)) && (!selectedCategory || item.category === selectedCategory);
        return isHighlighted || isCreatedVisible;
    });
    grid.onscroll = null;
    if (!visibleItems.length) {
        grid.innerHTML = '<p class="empty-state">Для выбранного фермера пока нет товаров.</p>';
        return;
    }

    let renderedCount = Math.min(assortmentBatchSize, visibleItems.length);
    const renderVisibleItems = (keepScrollPosition = false) => {
        const scrollTop = grid.scrollTop;
        grid.innerHTML = visibleItems
            .slice(0, renderedCount)
            .map((item) => productCardTemplate(item, eventByProduct))
            .join('');
        if (keepScrollPosition) {
            grid.scrollTop = scrollTop;
        }
    };

    renderVisibleItems();
    grid.onscroll = () => {
        const isNearBottom = grid.scrollTop + grid.clientHeight >= grid.scrollHeight - 160;
        if (isNearBottom && renderedCount < visibleItems.length) {
            renderedCount = Math.min(renderedCount + assortmentBatchSize, visibleItems.length);
            renderVisibleItems(true);
        }
    };
}

function productCardTemplate(item, eventByProduct) {
    const event = eventByProduct.get(item.name);
    const isCreated = state.createdProductIds.has(String(item.productId));
    const badge = event ? shortText(event.title, 28) : isCreated ? 'Добавлен в БД' : '';
    return `
        <article class="product-card ${event ? 'promoted' : ''} ${isCreated ? 'custom' : ''}">
            <div class="product-card-inner">
                ${badge ? `<span class="product-badge">${escapeHtml(badge)}</span>` : ''}
                <div class="product-image" style="background-image: url('${categoryImages[item.category] || categoryImages['Бакалея']}')"></div>
                <div>
                    <h3>${escapeHtml(shortText(item.name, 42))}</h3>
                    <p>${escapeHtml(item.category)}</p>
                    <strong>${formatRub(item.price)}</strong>
                    ${isCreated ? `<small>Остаток ${escapeHtml(item.stock)} шт. · ${escapeHtml(item.monthlySales)} продаж/мес.</small>` : ''}
                </div>
            </div>
        </article>
    `;
}

async function addCustomProduct(event) {
    event.preventDefault();
    const name = document.getElementById('custom-product-name').value.trim();
    const category = document.getElementById('custom-product-category').value;
    const organizationId = Number(document.getElementById('custom-product-farmer').value);
    if (!name || !category || !organizationId) {
        return;
    }

    const savedProduct = await sendJson(`${apiBase}/assortment`, 'POST', {
        organizationId,
        name,
        category,
        price: Math.max(1, Math.round(numberOrZero(document.getElementById('custom-product-price').value))),
        stock: Math.max(0, Math.round(numberOrZero(document.getElementById('custom-product-stock').value))),
        monthlySales: Math.max(0, Math.round(numberOrZero(document.getElementById('custom-product-sales').value))),
        productDescription: document.getElementById('custom-product-description').value.trim(),
        tags: [category, name]
    });

    state.createdProductIds.add(String(savedProduct.productId));
    state.selectedFarmerId = String(savedProduct.organizationId);
    updateFarmerAvatar();
    renderCustomProductCategoryOptions();
    resetCustomProductForm();
    await loadAssortment();
    if (state.calendarLoaded) {
        await loadCalendar();
    }
    showToast('Товар сохранён в БД');
}

function renderCustomProductCategoryOptions() {
    const select = document.getElementById('custom-product-category');
    if (!select) {
        return;
    }
    const categories = customProductCategories();
    select.innerHTML = categories.map((category) => `<option value="${escapeHtml(category)}">${escapeHtml(category)}</option>`).join('');
}

function customProductCategories() {
    const categories = new Set([
        ...Object.keys(categoryImages),
        ...state.assortment.map((item) => item.category),
        ...(state.calendar?.categories || [])
    ]);
    return [...categories].sort((a, b) => a.localeCompare(b, 'ru'));
}

function renderCustomProductFarmerOptions() {
    const select = document.getElementById('custom-product-farmer');
    if (!select) {
        return;
    }
    select.innerHTML = state.farmers.map((farmer) => `
        <option value="${escapeHtml(farmer.organizationId)}">${escapeHtml(farmer.shopName)} · ${escapeHtml(farmer.region)}</option>
    `).join('');
    if (state.selectedFarmerId && state.farmers.some((farmer) => String(farmer.organizationId) === String(state.selectedFarmerId))) {
        select.value = state.selectedFarmerId;
    }
}

function resetCustomProductForm() {
    const form = document.getElementById('custom-product-form');
    form.reset();
    document.getElementById('custom-product-price').value = '750';
    document.getElementById('custom-product-stock').value = '40';
    document.getElementById('custom-product-sales').value = '12';
    renderCustomProductFarmerOptions();
    renderCustomProductCategoryOptions();
}

function renderCalendar(calendar) {
    const summary = adjustedCalendarSummary(calendar);
    document.getElementById('calendar-summary').innerHTML = `
        <div class="summary-item"><strong>${summary.totalEvents}</strong><span>событий</span></div>
        <div class="summary-item"><strong>${summary.expectedOrders}</strong><span>заказов прогноз</span></div>
        <div class="summary-item"><strong>${summary.averageRoiPercent}%</strong><span>средний ROI</span></div>
        <div class="summary-item"><strong>${formatDate(calendar.periodStart)}</strong><span>старт периода</span></div>
        <button class="summary-export" type="button" data-export-plan>Экспорт плана</button>
    `;
    document.getElementById('recommendations').innerHTML = calendar.summary.recommendations.map((item) => `<li>${escapeHtml(item)}</li>`).join('');
    renderPlanningTools(calendar);

    const list = document.getElementById('calendar-list');
    if (!calendar.events.length) {
        list.innerHTML = '<article class="event-card empty"><p>Для выбранного фильтра нет событий. Попробуйте другую категорию или квартал.</p></article>';
        return;
    }

    list.innerHTML = calendar.events.map((event) => {
        const groups = channelGroups(event.channels);
        return `
            <article class="event-card">
                <div class="event-date">${monthName(event.date)}<span>${new Date(event.date).getDate()}</span><small>${escapeHtml(eventPeriod(event))}</small></div>
                <div>
                    <div class="event-title">
                        <h2>${escapeHtml(event.title)}</h2>
                        <span class="badge">${escapeHtml(event.eventType)}</span>
                        <span class="badge">${escapeHtml(event.category)}</span>
                        <span class="badge score ${eventScore(event).level}">${escapeHtml(eventScore(event).label)}</span>
                    </div>
                    <div class="product-spotlight">
                        <span>Рекомендуемый товар в момент события</span>
                        <strong>${escapeHtml(event.product)}</strong>
                        <details class="spotlight-details">
                            <summary>Подробнее</summary>
                            <p>${escapeHtml(event.rationale)}</p>
                        </details>
                    </div>
                    ${renderEventActions(event, groups)}
                </div>
            </article>
        `;
    }).join('');
}

function renderEventActions(event, groups) {
    const channelActionsSection = renderChannelActionsSection(event, groups.marketplace);
    const forecast = eventForecast(event);
    return `
        <div class="event-action-sections">
            ${channelActionsSection}

            <section class="event-action-section">
                <header class="event-action-heading">
                    <span class="action-icon">%</span>
                    <h3>Что предложить и сделать</h3>
                </header>
                <div class="action-pair-grid">
                    <article class="event-action-mini">
                        <h4>Оффер для покупателя</h4>
                        ${detailsText(textWithCurrentPromoSettings(event.offer, event))}
                        ${discountPromoControls(event)}
                    </article>
                    <article class="event-action-mini">
                        <h4>Идея для описания товара</h4>
                        ${detailsText(textWithCurrentPromoSettings(event.marketplaceAction, event))}
                    </article>
                </div>
            </section>

            ${aiContentIdeasSection(event)}

            ${campaignDecisionSection(event)}

            <section class="event-action-section materials-section">
                <header class="event-action-heading materials-heading">
                    <span class="action-icon">↗</span>
                    <h3>Прогноз</h3>
                </header>
                <div class="forecast-card">
                    <div class="forecast-lead">
                        <span>${forecastConclusion(forecast)}</span>
                        <strong>${forecast.expectedOrders} заказов</strong>
                        <p>${escapeHtml(event.evidence)}</p>
                    </div>
                    <div class="forecast-metrics">
                        <article>
                            <span>Рост спроса</span>
                            <strong>${signedPercent(forecast.upliftPercent)}</strong>
                            <small>база ${forecast.baselineOrders}, сезон ${forecast.seasonalOrders}</small>
                        </article>
                        <article>
                            <span>ROI акции</span>
                            <strong>${forecast.roiPercent}%</strong>
                            <small>${forecast.roiPercent >= 0 ? 'акция окупается' : 'нужно снизить затраты'}</small>
                        </article>
                        <article>
                            <span>Остаток</span>
                            <strong>${forecast.stock} шт.</strong>
                            <small>${forecast.stockLimited ? 'спрос выше остатка' : 'хватает под прогноз'}</small>
                        </article>
                    </div>
                    <div class="forecast-breakdown">
                        <div><span>Цена</span><strong>${formatRubSafe(event.productPrice)}</strong></div>
                        <div><span>Скидка</span><strong>${forecast.discountPercent}%</strong></div>
                        <div><span>Маржа</span><strong>${forecast.marginPercent}%</strong></div>
                        <div><span>Выручка</span><strong>${formatRubSafe(forecast.grossRevenue)}</strong></div>
                        <div><span>Валовая прибыль</span><strong>${formatRubSafe(forecast.grossProfit)}</strong></div>
                        <div><span>Затраты</span><strong>${formatRubSafe(forecast.marketingCost)}</strong></div>
                    </div>
                    <div class="metrics-ai" data-metrics-analysis-surface>
                        <p class="social-post-text metrics-analysis-text" data-generated-metrics-analysis hidden></p>
                        <button class="generate-text-button metrics-analysis-button" type="button"
                                data-analyze-metrics
                                data-metric-event-title="${escapeHtml(event.title)}"
                                data-metric-event-type="${escapeHtml(event.eventType)}"
                                data-metric-product="${escapeHtml(event.product)}"
                                data-metric-category="${escapeHtml(event.category)}"
                                data-metric-baseline-orders="${escapeHtml(forecast.baselineOrders)}"
                                data-metric-seasonal-orders="${escapeHtml(forecast.seasonalOrders)}"
                                data-metric-expected-orders="${escapeHtml(forecast.expectedOrders)}"
                                data-metric-uplift="${escapeHtml(forecast.upliftPercent)}"
                                data-metric-roi="${escapeHtml(forecast.roiPercent)}"
                                data-metric-stock="${escapeHtml(forecast.stock)}"
                                data-metric-stock-limited="${escapeHtml(forecast.stockLimited)}"
                                data-metric-revenue="${escapeHtml(forecast.grossRevenue)}"
                                data-metric-profit="${escapeHtml(forecast.grossProfit)}"
                                data-metric-cost="${escapeHtml(forecast.marketingCost)}">
                            Проанализировать метрики
                        </button>
                    </div>
                </div>
            </section>

            ${historySection(event)}
        </div>
    `;
}

function forecastConclusion(forecast) {
    if (forecast.stockLimited) {
        return 'Спрос есть, но продажи ограничит остаток';
    }
    if (forecast.roiPercent >= 80 && forecast.upliftPercent > 0) {
        return 'Хороший кандидат для запуска';
    }
    if (forecast.roiPercent < 0) {
        return 'Запускать осторожно';
    }
    return 'Прогноз стабильный';
}

function forecastDemandLine(event) {
    const forecast = eventForecast(event);
    const stockNote = forecast.stockLimited ? ' · ограничено остатком' : '';
    return `База: ${forecast.baselineOrders}; сезонный спрос: ${forecast.seasonalOrders}; рост: ${signedPercent(forecast.upliftPercent)}${stockNote}`;
}

function forecastEconomicsLine(event) {
    const forecast = eventForecast(event);
    return `Цена: ${formatRubSafe(event.productPrice)} · остаток: ${forecast.stock} шт. · скидка: ${forecast.discountPercent}% · маржа: ${forecast.marginPercent}%`;
}

function forecastCostLine(event) {
    const forecast = eventForecast(event);
    return `Выручка: ${formatRubSafe(forecast.grossRevenue)} · валовая прибыль: ${formatRubSafe(forecast.grossProfit)} · затраты: ${formatRubSafe(forecast.marketingCost)} (${formatRubSafe(forecast.discountCost)} скидка, ${formatRubSafe(forecast.operationalCost)} операц.)`;
}

function adjustedCalendarSummary(calendar) {
    const events = calendar.events || [];
    let expectedOrders = 0;
    let roiSum = 0;
    events.forEach((event) => {
        const forecast = eventForecast(event);
        expectedOrders += forecast.expectedOrders;
        roiSum += forecast.roiPercent;
    });
    return {
        ...calendar.summary,
        totalEvents: events.length,
        expectedOrders,
        averageRoiPercent: events.length ? Math.round(roiSum / events.length) : 0
    };
}

function eventForecast(event, discountOverride = null) {
    const baselineOrders = Math.round(numberOrZero(event.baselineOrders));
    const seasonalOrders = Math.round(numberOrZero(event.seasonalOrders));
    const stock = Math.round(numberOrZero(event.stock));
    const price = numberOrZero(event.productPrice);
    const discountPercent = discountOverride === null ? currentDiscountPercent(event) : discountOverride;
    const marginPercent = Math.round(numberOrZero(event.marginPercent));
    const expectedDemand = Math.max(8, Math.round(seasonalOrders * discountDemandMultiplier(discountPercent)));
    const expectedOrders = Math.min(Math.max(0, stock), expectedDemand);
    const upliftPercent = baselineOrders <= 0
        ? (expectedOrders > 0 ? 100 : 0)
        : Math.round((expectedOrders - baselineOrders) * 100 / baselineOrders);
    const discountRate = discountPercent / 100;
    const marginRate = marginPercent / 100;
    const fullRevenue = roundedMoney(price * expectedOrders);
    const grossRevenue = roundedMoney(fullRevenue * (1 - discountRate));
    const baselineRevenue = roundedMoney(price * baselineOrders);
    const grossProfit = roundedMoney(grossRevenue * marginRate);
    const baselineGrossProfit = roundedMoney(baselineRevenue * marginRate);
    const incrementalGrossProfit = grossProfit - baselineGrossProfit;
    const discountCost = roundedMoney(fullRevenue * discountRate);
    const operationalCost = roundedMoney(grossRevenue * 0.03);
    const marketingCost = discountCost + operationalCost;
    const netIncrementalProfit = incrementalGrossProfit - operationalCost;
    const roiPercent = marketingCost === 0 ? 0 : Math.round(netIncrementalProfit * 100 / marketingCost);

    return {
        baselineOrders,
        seasonalOrders,
        expectedOrders,
        upliftPercent,
        roiPercent,
        discountPercent,
        marginPercent,
        stock,
        stockLimited: stock < expectedDemand,
        grossRevenue,
        grossProfit,
        marketingCost,
        discountCost,
        operationalCost
    };
}

function discountDemandMultiplier(discountPercent) {
    const discount = numberOrZero(discountPercent);
    if (discount <= 0) {
        return 1;
    }
    return 1 + Math.min(0.42, discount * 0.012);
}

function roundedMoney(value) {
    return Math.round(numberOrZero(value));
}

function campaignKey(event) {
    return adviceKey(event, 'campaign');
}

function renderPlanningTools(calendar) {
    const panel = document.getElementById('planning-tools');
    if (!panel || !calendar || !calendar.events.length) {
        if (panel) {
            panel.hidden = true;
        }
        return;
    }
    panel.hidden = false;
    syncPlanningDefaults(calendar);
    panel.innerHTML = `
        <header class="panel-head planning-head">
            <div>
                <h2>Планирование продаж</h2>
                <p>Приоритизация, калькулятор акций, рейтинг товаров и сезонная карта года.</p>
            </div>
        </header>
        <div class="planning-grid">
            ${monthlyPrioritiesSection(calendar)}
            ${calculatorSection(calendar)}
            ${productRankingSection(calendar)}
            ${seasonalMapSection(calendar)}
        </div>
    `;
}

function syncPlanningDefaults(calendar) {
    const events = calendar.events || [];
    if (!events.length) {
        return;
    }
    const selected = events.find((event) => campaignKey(event) === state.calculator.eventKey) || events[0];
    const forecast = eventForecast(selected);
    state.calculator.eventKey = campaignKey(selected);
    state.calculator.discountPercent = normalizeDiscountPercent(state.calculator.discountPercent) ?? forecast.discountPercent;
    state.calculator.stock = Math.max(0, Math.round(numberOrZero(state.calculator.stock || forecast.stock)));
    state.calculator.price = Math.max(1, Math.round(numberOrZero(state.calculator.price || selected.productPrice)));
    state.calculator.marginPercent = Math.max(1, Math.min(90, Math.round(numberOrZero(state.calculator.marginPercent || forecast.marginPercent))));
}

function monthlyPrioritiesSection(calendar) {
    const items = campaignPriorityItems(calendar.events);
    return `
        <section class="planning-card priorities-card">
            <header>
                <span>01</span>
                <h3>Автоприоритет месяца</h3>
            </header>
            <div class="priority-list">
                ${items.map((item) => `
                    <article class="${item.level}">
                        <strong>${escapeHtml(item.label)}</strong>
                        <span>${escapeHtml(item.event.title)}</span>
                        <p>${escapeHtml(item.reason)}</p>
                    </article>
                `).join('')}
            </div>
        </section>
    `;
}

function campaignPriorityItems(events) {
    const ranked = [...events]
        .map((event) => ({event, score: eventScore(event).score, forecast: eventForecast(event), stock: stockRisk(event)}))
        .sort((a, b) => b.score - a.score || b.forecast.expectedOrders - a.forecast.expectedOrders);
    const must = ranked[0];
    const can = ranked.find((item) => item !== must && item.score >= 50) || ranked[1] || ranked[0];
    const skip = [...ranked].reverse().find((item) => item.stock.level === 'risk' || item.score < 50) || ranked[ranked.length - 1] || ranked[0];
    return [
        {
            level: 'must',
            label: 'Запустить обязательно',
            event: must.event,
            reason: `ROI ${must.forecast.roiPercent}%, ${must.forecast.expectedOrders} заказов, ${eventScore(must.event).label.toLowerCase()}.`
        },
        {
            level: 'can',
            label: 'Можно запускать',
            event: can.event,
            reason: `Хороший запас по спросу: рост ${signedPercent(can.forecast.upliftPercent)}.`
        },
        {
            level: 'skip',
            label: 'Проверить перед запуском',
            event: skip.event,
            reason: `${skip.stock.title}: ${skip.stock.note}`
        }
    ];
}

function calculatorSection(calendar) {
    const selected = calendar.events.find((event) => campaignKey(event) === state.calculator.eventKey) || calendar.events[0];
    const result = calculatorForecast(selected);
    return `
        <section class="planning-card calculator-card">
            <header>
                <span>02</span>
                <h3>Калькулятор акций</h3>
            </header>
            <div class="tool-form">
                <label>Акция
                    <select data-calculator-field="eventKey">
                        ${calendar.events.map((event) => `<option value="${escapeHtml(campaignKey(event))}" ${campaignKey(event) === state.calculator.eventKey ? 'selected' : ''}>${escapeHtml(shortText(event.title, 42))}</option>`).join('')}
                    </select>
                </label>
                <label>Скидка, %
                    <input type="number" min="0" max="90" step="1" value="${escapeHtml(state.calculator.discountPercent)}" data-calculator-field="discountPercent">
                </label>
                <label>Остаток
                    <input type="number" min="0" step="1" value="${escapeHtml(state.calculator.stock)}" data-calculator-field="stock">
                </label>
                <label>Цена, ₽
                    <input type="number" min="1" step="1" value="${escapeHtml(state.calculator.price)}" data-calculator-field="price">
                </label>
                <label>Маржа, %
                    <input type="number" min="1" max="90" step="1" value="${escapeHtml(state.calculator.marginPercent)}" data-calculator-field="marginPercent">
                </label>
            </div>
            <div class="simulation-result">
                <strong>${result.expectedOrders} заказов · ROI ${result.roiPercent}%</strong>
                <span>Выручка ${formatRubSafe(result.grossRevenue)}, затраты ${formatRubSafe(result.marketingCost)}</span>
            </div>
        </section>
    `;
}

function productRankingSection(calendar) {
    const ranked = productRanking(calendar).slice(0, 6);
    return `
        <section class="planning-card ranking-card">
            <header>
                <span>03</span>
                <h3>Рейтинг товаров</h3>
            </header>
            <div class="ranking-list">
                ${ranked.map((item, index) => `
                    <article>
                        <b>${index + 1}</b>
                        <div>
                            <strong>${escapeHtml(shortText(item.name, 40))}</strong>
                            <span>${escapeHtml(item.category)} · потенциал ${item.score}</span>
                        </div>
                    </article>
                `).join('')}
            </div>
        </section>
    `;
}

function seasonalMapSection(calendar) {
    const cells = seasonalYearCells(calendar);
    return `
        <section class="planning-card seasonal-card">
            <header>
                <span>04</span>
                <h3>Сезонная карта года</h3>
            </header>
            <div class="seasonal-map">
                ${cells.map((cell) => `
                    <article class="${cell.level}">
                        <strong>${escapeHtml(cell.month)}</strong>
                        <span>${escapeHtml(cell.category)}</span>
                        <small>${cell.score}</small>
                    </article>
                `).join('')}
            </div>
        </section>
    `;
}

function updateCalculatorState() {
    const panel = document.getElementById('planning-tools');
    if (!panel) {
        return;
    }
    const previousEventKey = state.calculator.eventKey;
    panel.querySelectorAll('[data-calculator-field]').forEach((field) => {
        const key = field.dataset.calculatorField;
        if (key === 'eventKey') {
            state.calculator.eventKey = field.value;
        } else {
            state.calculator[key] = Math.round(numberOrZero(field.value));
        }
    });
    if (state.calculator.eventKey !== previousEventKey && state.calendar) {
        const selected = state.calendar.events.find((event) => campaignKey(event) === state.calculator.eventKey);
        if (selected) {
            const forecast = eventForecast(selected);
            state.calculator.discountPercent = forecast.discountPercent;
            state.calculator.stock = forecast.stock;
            state.calculator.price = Math.max(1, Math.round(numberOrZero(selected.productPrice)));
            state.calculator.marginPercent = forecast.marginPercent;
        }
    }
    state.calculator.discountPercent = Math.max(0, Math.min(90, state.calculator.discountPercent));
    state.calculator.stock = Math.max(0, state.calculator.stock);
    state.calculator.price = Math.max(1, state.calculator.price);
    state.calculator.marginPercent = Math.max(1, Math.min(90, state.calculator.marginPercent));
}

function calculatorForecast(event) {
    const synthetic = {
        ...event,
        stock: state.calculator.stock,
        productPrice: state.calculator.price,
        marginPercent: state.calculator.marginPercent
    };
    return eventForecast(synthetic, state.calculator.discountPercent);
}

function productRanking(calendar) {
    const eventByProduct = new Map((calendar.events || []).map((event) => [event.product, event]));
    return state.assortment.map((item) => {
        const event = eventByProduct.get(item.name);
        const monthlySales = numberOrZero(item.monthlySales);
        const stock = numberOrZero(item.stock);
        const baseScore = Math.round(monthlySales * 1.4 + Math.min(stock, 120) * 0.35);
        const eventScoreValue = event ? eventScore(event).score + Math.max(0, eventForecast(event).roiPercent / 3) : 0;
        return {
            ...item,
            score: Math.round(baseScore + eventScoreValue)
        };
    })
        .sort((a, b) => b.score - a.score);
}

function seasonalYearCells(calendar) {
    const categories = categorySeasonProfiles();
    const eventMonths = new Map();
    (calendar.events || []).forEach((event) => {
        const month = new Date(event.date).getMonth() + 1;
        const current = eventMonths.get(month) || {score: 0, category: event.category};
        current.score += Math.max(1, Math.round(eventScore(event).score / 20));
        current.category = event.category;
        eventMonths.set(month, current);
    });

    return Array.from({length: 12}, (_, index) => {
        const month = index + 1;
        const profile = categories[month] || {category: 'Смешанный спрос', score: 1};
        const eventBoost = eventMonths.get(month);
        const score = profile.score + (eventBoost?.score || 0);
        return {
            month: shortMonthName(month),
            category: eventBoost?.category || profile.category,
            score,
            level: score >= 5 ? 'hot' : score >= 3 ? 'warm' : 'calm'
        };
    });
}

function categorySeasonProfiles() {
    return {
        1: {category: 'Мясо, молочка', score: 4},
        2: {category: 'Сладости, подарки', score: 4},
        3: {category: 'Сладости, сыры', score: 5},
        4: {category: 'Яйца, выпечка', score: 4},
        5: {category: 'Пикники, мясо', score: 5},
        6: {category: 'Овощи, ягоды', score: 5},
        7: {category: 'Фрукты, напитки', score: 4},
        8: {category: 'Овощи, заготовки', score: 5},
        9: {category: 'Бакалея, мёд', score: 4},
        10: {category: 'Сыры, чай', score: 3},
        11: {category: 'Подарки, премиум', score: 4},
        12: {category: 'Наборы, деликатесы', score: 6}
    };
}

function campaignDecisionSection(event) {
    const discount = recommendedDiscount(event);
    const stock = stockRisk(event);
    const segment = segmentRecommendation(event);
    const bundles = bundleRecommendations(event, 3);
    const fallback = bundles.length ? null : bundleRecommendation(event);
    return `
        <section class="event-action-section">
            <header class="event-action-heading">
                <span class="action-icon">◆</span>
                <h3>Решение по акции</h3>
            </header>
            <div class="decision-grid">
                <article class="decision-card">
                    <span>Скидка</span>
                    <strong>${discount.discountPercent}%</strong>
                    ${detailsText(discount.note)}
                </article>
                <article class="decision-card ${stock.level}">
                    <span>Остатки</span>
                    <strong>${escapeHtml(stock.title)}</strong>
                    ${detailsText(stock.note)}
                </article>
                <article class="decision-card">
                    <span>Сегмент</span>
                    <strong>${escapeHtml(segment.title)}</strong>
                    ${detailsText(segment.note)}
                </article>
                <article class="decision-card bundle-card">
                    <span>Набор</span>
                    ${bundles.length ? `
                        <details class="bundle-details">
                            <summary>Показать варианты наборов</summary>
                            <div class="bundle-options">
                                ${bundles.map((bundle) => `
                                    <div class="bundle-option">
                                        <strong>${escapeHtml(bundle.title)}</strong>
                                        ${detailsText(bundle.note)}
                                    </div>
                                `).join('')}
                            </div>
                        </details>
                    ` : `
                        <strong>${escapeHtml(fallback.title)}</strong>
                        ${detailsText(fallback.note)}
                    `}
                </article>
            </div>
        </section>
    `;
}

function historySection(event) {
    const items = eventHistory(event).slice(0, 4);
    if (!items.length) {
        return '';
    }
    return `
        <section class="event-action-section compact-section">
            <header class="event-action-heading">
                <span class="action-icon">↺</span>
                <h3>История изменений</h3>
            </header>
            <details class="history-details">
                <summary>Показать историю</summary>
                <div class="history-list">
                    ${items.map((item) => `
                        <p><strong>${escapeHtml(historyActionLabel(item.actionType))}</strong> ${escapeHtml(item.details)} <span>${escapeHtml(formatHistoryDate(item.createdAt))}</span></p>
                    `).join('')}
                </div>
            </details>
        </section>
    `;
}

function eventScore(event) {
    const forecast = eventForecast(event);
    let score = 45;
    score += Math.min(25, Math.max(-15, forecast.roiPercent / 4));
    score += Math.min(18, Math.max(-10, forecast.upliftPercent / 5));
    if (forecast.stockLimited) {
        score -= 12;
    }
    if (forecast.marginPercent >= 33) {
        score += 8;
    }
    score = Math.max(0, Math.min(100, Math.round(score)));
    if (score >= 72) {
        return {score, label: 'Сильное событие', level: 'strong'};
    }
    if (score >= 50) {
        return {score, label: 'Среднее событие', level: 'medium'};
    }
    return {score, label: 'Рискованно', level: 'risk'};
}

function recommendedDiscount(event) {
    const best = bestDiscountCandidate(event);
    const current = currentDiscountPercent(event);
    const note = current === best.discount
            ? `Оптимально для спроса и маржи: ROI ${best.forecast.roiPercent}% при ${best.forecast.expectedOrders} заказах.`
            : `Рекомендуем ${best.discount}%: ROI ${best.forecast.roiPercent}% при ${best.forecast.expectedOrders} заказах.`;
    return {discountPercent: best.discount, note};
}

function bestDiscountCandidate(event) {
    if (numberOrZero(event.stock) <= 0) {
        return {discount: 0, forecast: eventForecast(event, 0)};
    }

    const baseline = eventForecast(event, 0);
    const target = targetDiscountPercent(event, baseline);
    return discountOptionsForEvent(event).map((discount) => {
        const forecast = eventForecast(event, discount);
        const demandGain = Math.max(0, forecast.expectedOrders - baseline.expectedOrders);
        const roiPenalty = forecast.roiPercent < 0 ? 180 : 0;
        const stockPenalty = forecast.stockLimited ? 18 : 0;
        const score = forecast.roiPercent * 0.45
                + forecast.upliftPercent * 1.2
                + demandGain * 3
                + forecast.expectedOrders * 0.6
                - Math.abs(discount - target) * 5
                - discount * 0.4
                - roiPenalty
                - stockPenalty;
        return {discount, forecast, score};
    }).sort((a, b) => b.score - a.score || b.forecast.roiPercent - a.forecast.roiPercent)[0];
}

function discountOptionsForEvent(event) {
    const margin = Math.round(numberOrZero(event.marginPercent));
    const maxByMargin = margin < 24 ? 7 : margin < 34 ? 12 : margin < 48 ? 15 : margin < 64 ? 20 : 25;
    return [5, 7, 10, 12, 15, 20, 25].filter((discount) => discount <= maxByMargin);
}

function targetDiscountPercent(event, baseline) {
    let target = event.eventType === 'Праздник' ? 12 : event.eventType === 'Тренд' ? 10 : 7;
    const category = String(event.category || '').toLowerCase();
    const margin = Math.round(numberOrZero(event.marginPercent));
    const stock = Math.round(numberOrZero(event.stock));

    if (category.includes('слад') || category.includes('мед') || category.includes('мёд') || category.includes('подар')) {
        target += 2;
    }
    if (stock > Math.max(24, baseline.expectedOrders * 2)) {
        target += 3;
    }
    if (stock <= baseline.expectedOrders) {
        target -= 3;
    }
    if (margin >= 45) {
        target += 2;
    }
    if (margin < 28) {
        target -= 3;
    }

    const options = discountOptionsForEvent(event);
    return options.reduce((nearest, discount) =>
            Math.abs(discount - target) < Math.abs(nearest - target) ? discount : nearest, options[0]);
}

function stockRisk(event) {
    const forecast = eventForecast(event);
    if (forecast.stockLimited) {
        return {
            level: 'risk',
            title: 'Не хватит',
            note: `Остаток ${forecast.stock} шт. ниже ожидаемого спроса. Нужен предзаказ или снижение охвата.`
        };
    }
    if (forecast.stock > Math.max(20, forecast.expectedOrders * 2.5)) {
        return {
            level: 'warning',
            title: 'Излишек',
            note: `После прогноза может остаться около ${forecast.stock - forecast.expectedOrders} шт.; стоит добавить набор или повторный промокод.`
        };
    }
    return {
        level: 'strong',
        title: 'Норма',
        note: `Остатка хватает на прогноз и небольшой запас: ${forecast.stock} шт.`
    };
}

function segmentRecommendation(event) {
    const category = String(event.category || '').toLowerCase();
    if (event.eventType === 'Праздник' || category.includes('слад') || category.includes('мед') || category.includes('мёд')) {
        return {title: 'Подарочные покупатели', note: 'Показывать тем, кто брал наборы, сладости, мёд или премиальные товары.'};
    }
    if (category.includes('молоч') || category.includes('яйц') || category.includes('хлеб')) {
        return {title: 'Повторные клиенты', note: 'Лучше работает аудитория регулярных заказов и семейных корзин.'};
    }
    if (event.eventType === 'Локальное событие') {
        return {title: 'Покупатели региона', note: 'Сузить коммуникацию до региона фермера и соседних точек выдачи.'};
    }
    return {title: 'Покупатели категории', note: `Запустить по тем, кто смотрел или покупал категорию ${event.category}.`};
}

function bundleRecommendation(event) {
    return bundleRecommendations(event, 1)[0] || fallbackBundle(event, bundleKind(event, eventAssortmentItem(event)), eventAssortmentItem(event));
}

function bundleRecommendations(event, limit = 3) {
    const mainItem = eventAssortmentItem(event);
    const kind = bundleKind(event, mainItem);
    return bestBundlePlans(event, mainItem, kind, limit).map((recommendation) => ({
        title: `${recommendation.plan.title}: ${recommendation.items.map((item) => item.name).join(' + ')}`,
        note: bundleRecommendationNote(event, recommendation)
    }));
}

function eventAssortmentItem(event) {
    return state.assortment.find((item) => event.productId && item.productId === event.productId)
        || state.assortment.find((item) => event.organizationId && item.organizationId === event.organizationId && item.name === event.product)
        || state.assortment.find((item) => item.name === event.product)
        || null;
}

function bundleKind(event, item) {
    const value = bundleText([event.category, event.product, item?.category, item?.name, item?.productDescription]);
    if (containsAny(value, ['говяд', 'свинин', 'баранин', 'курин', 'индей', 'мясн', 'мясо', 'стейк', 'ростбиф', 'тушен', 'колбас', 'mjas'])) {
        return 'meat';
    }
    if (containsAny(value, ['рыб', 'морепродукт', 'икра'])) {
        return 'fish';
    }
    if (containsAny(value, ['сыр ', 'сыры', 'брынз', 'рикотт'])) {
        return 'cheese';
    }
    if (containsAny(value, ['молоч', 'йогурт', 'творог', 'сметан', 'молоко', 'яйц'])) {
        return 'dairy';
    }
    if (containsAny(value, ['овощ', 'фрукт', 'ягод', 'томат', 'картоф'])) {
        return 'produce';
    }
    if (containsAny(value, ['слад', 'шоколад', 'десерт', 'паста', 'варенье', 'мармелад'])) {
        return 'sweet';
    }
    if (containsAny(value, ['мед ', 'мёд ', 'honey'])) {
        return 'honey';
    }
    if (containsAny(value, ['напит', 'чай', 'сок', 'морс', 'квас'])) {
        return 'drink';
    }
    return 'pantry';
}

function bundlePlans(kind) {
    const plans = {
        meat: [
            {
                title: 'Гриль-набор',
                note: 'Мясо лучше продавать со свежим гарниром и понятным сценарием ужина.',
                slots: [
                    {categories: ['Овощи и фрукты'], include: ['томат', 'картоф', 'перец', 'овощ', 'зелень', 'гриб', 'лук', 'кабач', 'баклаж', 'огур']},
                    {categories: ['Сыры', 'Хлеб и выпечка', 'Напитки', 'Бакалея'], include: ['соус', 'аджик', 'горч', 'масло', 'спец', 'маринад', 'сыр', 'хлеб', 'лаваш', 'квас', 'морс', 'сок'], exclude: ['мука', 'арахис', 'урбеч', 'паста', 'слад', 'круп', 'чай']}
                ]
            },
            {
                title: 'Ростбиф-сэндвич',
                note: 'Для готового мясного SKU лучше работает сценарий быстрого перекуса.',
                slots: [
                    {categories: ['Хлеб и выпечка'], include: ['хлеб', 'лаваш', 'булоч', 'лепеш', 'багет']},
                    {categories: ['Сыры', 'Овощи и фрукты'], include: ['сыр', 'томат', 'огур', 'зелень', 'перец']}
                ]
            },
            {
                title: 'Стейк-ужин',
                note: 'Набор продает не один SKU, а полноценный ужин с гарниром.',
                slots: [
                    {categories: ['Овощи и фрукты'], include: ['картоф', 'томат', 'перец', 'гриб', 'лук', 'овощ']},
                    {categories: ['Напитки', 'Сыры'], include: ['морс', 'квас', 'сок', 'сыр']}
                ]
            },
            {
                title: 'Пикник',
                note: 'Для летних событий мясо лучше связывать с продуктами, которые берут на пикник.',
                slots: [
                    {categories: ['Овощи и фрукты'], include: ['огур', 'томат', 'зелень', 'овощ', 'перец']},
                    {categories: ['Хлеб и выпечка', 'Напитки'], include: ['лаваш', 'хлеб', 'квас', 'морс', 'сок']}
                ]
            },
            {
                title: 'Деликатесный сет',
                note: 'Премиальное мясо стоит усиливать сыром, хлебом или напитком, а не случайной бакалеей.',
                slots: [
                    {categories: ['Сыры', 'Хлеб и выпечка'], include: ['сыр', 'брынз', 'хлеб', 'лаваш', 'булоч']},
                    {categories: ['Овощи и фрукты', 'Напитки'], include: ['томат', 'зелень', 'перец', 'морс', 'квас', 'сок']}
                ]
            },
            {
                title: 'Ужин без готовки',
                note: 'Набор закрывает быстрый прием пищи: основной мясной SKU плюс свежий продукт или напиток.',
                slots: [
                    {categories: ['Хлеб и выпечка', 'Овощи и фрукты'], include: ['хлеб', 'лаваш', 'томат', 'огур', 'зелень']},
                    {categories: ['Напитки', 'Сыры'], include: ['морс', 'квас', 'сок', 'сыр']}
                ]
            },
            {
                title: 'Мясная витрина',
                note: 'Если у продавца есть несколько мясных SKU, можно собрать линейку для сравнения вкусов.',
                slots: [
                    {categories: ['Мясо и птица'], include: ['говяд', 'свинин', 'курин', 'индей', 'колбас', 'мяс']},
                    {categories: ['Овощи и фрукты', 'Хлеб и выпечка'], include: ['томат', 'зелень', 'хлеб', 'лаваш']}
                ]
            }
        ],
        fish: [
            {
                title: 'Рыбный ужин',
                note: 'Рыбу логично сочетать с овощами, хлебом или легким напитком.',
                slots: [
                    {categories: ['Овощи и фрукты'], include: ['картоф', 'томат', 'зелень', 'огур', 'лимон']},
                    {categories: ['Хлеб и выпечка', 'Напитки', 'Бакалея'], include: ['хлеб', 'масло', 'соус', 'морс', 'сок'], exclude: ['мука', 'арахис', 'слад']}
                ]
            },
            {
                title: 'Закуска к столу',
                note: 'Такой набор удобно продвигать как готовую закуску к событию.',
                slots: [
                    {categories: ['Сыры', 'Хлеб и выпечка'], include: ['сыр', 'хлеб', 'лаваш']},
                    {categories: ['Овощи и фрукты', 'Напитки'], include: ['зелень', 'огур', 'томат', 'морс', 'квас']}
                ]
            },
            {
                title: 'Легкий обед',
                note: 'Рыбный SKU можно продавать как быстрый полезный обед.',
                slots: [
                    {categories: ['Овощи и фрукты'], include: ['салат', 'огур', 'томат', 'зелень', 'овощ']},
                    {categories: ['Бакалея', 'Хлеб и выпечка'], include: ['масло', 'соус', 'хлеб', 'лаваш'], exclude: ['мука', 'арахис', 'слад']}
                ]
            }
        ],
        cheese: [
            {
                title: 'Сырная тарелка',
                note: 'Сыр продается понятнее, когда рядом есть сладкий акцент или хлеб.',
                slots: [
                    {categories: ['Мед и пчеловодство', 'Сладости'], include: ['мед', 'мёд', 'варенье', 'джем', 'мармелад']},
                    {categories: ['Хлеб и выпечка', 'Напитки'], include: ['хлеб', 'лаваш', 'чай', 'сок', 'морс']}
                ]
            },
            {
                title: 'Завтрак',
                note: 'Набор подходит для регулярной корзины и повышает частоту повторных заказов.',
                slots: [
                    {categories: ['Хлеб и выпечка', 'Овощи и фрукты'], include: ['хлеб', 'булоч', 'томат', 'зелень']},
                    {categories: ['Напитки', 'Мед и пчеловодство'], include: ['чай', 'сок', 'мед', 'мёд']}
                ]
            },
            {
                title: 'Пикник с сыром',
                note: 'Сыр хорошо усиливают овощи, хлеб и напиток для готового сценария.',
                slots: [
                    {categories: ['Овощи и фрукты'], include: ['томат', 'огур', 'зелень', 'фрукт', 'ягод']},
                    {categories: ['Хлеб и выпечка', 'Напитки'], include: ['хлеб', 'лаваш', 'морс', 'сок', 'квас']}
                ]
            }
        ],
        dairy: [
            {
                title: 'Семейный завтрак',
                note: 'Молочные товары лучше работают в наборе с выпечкой, медом или сладким дополнением.',
                slots: [
                    {categories: ['Хлеб и выпечка', 'Бакалея'], include: ['хлеб', 'булоч', 'блин', 'гранола', 'хлопья']},
                    {categories: ['Мед и пчеловодство', 'Сладости', 'Овощи и фрукты'], include: ['мед', 'мёд', 'варенье', 'ягод', 'фрукт']}
                ]
            },
            {
                title: 'Полезный перекус',
                note: 'Молочные продукты удобнее продавать вместе с ягодами, фруктами или гранолой.',
                slots: [
                    {categories: ['Овощи и фрукты'], include: ['ягод', 'фрукт', 'яблок', 'груш']},
                    {categories: ['Бакалея', 'Сладости'], include: ['гранола', 'хлопья', 'мюсли', 'варенье', 'джем']}
                ]
            },
            {
                title: 'Блины и завтрак',
                note: 'Сценарий завтрака делает молочный SKU понятным для повторной покупки.',
                slots: [
                    {categories: ['Хлеб и выпечка', 'Бакалея'], include: ['блин', 'мука', 'хлеб', 'булоч']},
                    {categories: ['Мед и пчеловодство', 'Сладости'], include: ['мед', 'мёд', 'варенье', 'джем']}
                ]
            }
        ],
        produce: [
            {
                title: 'Свежая корзина',
                note: 'Овощи и фрукты хорошо дополняются сыром, хлебом или мясным SKU.',
                slots: [
                    {categories: ['Сыры', 'Мясо и птица'], include: ['сыр', 'говяд', 'курин', 'колбас']},
                    {categories: ['Хлеб и выпечка', 'Бакалея'], include: ['хлеб', 'масло', 'соус'], exclude: ['мука', 'арахис']}
                ]
            },
            {
                title: 'Салатный набор',
                note: 'Свежие овощи лучше продавать как готовый салатный сценарий.',
                slots: [
                    {categories: ['Сыры', 'Яйца и молочные продукты'], include: ['сыр', 'брынз', 'яйц', 'сметан']},
                    {categories: ['Бакалея'], include: ['масло', 'соус', 'спец'], exclude: ['мука', 'арахис', 'слад']}
                ]
            },
            {
                title: 'К ужину',
                note: 'Овощной SKU может стать гарниром к мясу или рыбе продавца.',
                slots: [
                    {categories: ['Мясо и птица', 'Рыба и морепродукты'], include: ['говяд', 'курин', 'рыб', 'мяс', 'стейк']},
                    {categories: ['Хлеб и выпечка', 'Напитки'], include: ['хлеб', 'лаваш', 'морс', 'квас']}
                ]
            }
        ],
        sweet: [
            {
                title: 'Подарочный набор',
                note: 'Сладости лучше продвигать как готовый подарок или чайную корзину.',
                slots: [
                    {categories: ['Напитки', 'Бакалея'], include: ['чай', 'кофе', 'сок']},
                    {categories: ['Мед и пчеловодство', 'Сладости'], include: ['мед', 'мёд', 'варенье', 'мармелад', 'шоколад']}
                ]
            },
            {
                title: 'Десерт к завтраку',
                note: 'Сладкий SKU хорошо добавляется к молочке или выпечке.',
                slots: [
                    {categories: ['Яйца и молочные продукты'], include: ['творог', 'йогурт', 'молоко', 'сметан']},
                    {categories: ['Хлеб и выпечка', 'Напитки'], include: ['булоч', 'хлеб', 'чай', 'кофе']}
                ]
            },
            {
                title: 'Чайная корзина',
                note: 'Сладости понятнее продавать как готовое дополнение к чаю.',
                slots: [
                    {categories: ['Бакалея', 'Напитки'], include: ['чай', 'кофе']},
                    {categories: ['Мед и пчеловодство', 'Хлеб и выпечка'], include: ['мед', 'мёд', 'печен', 'булоч']}
                ]
            }
        ],
        honey: [
            {
                title: 'Чайный набор',
                note: 'Меду нужен понятный повод: чай, выпечка или сладкий подарок.',
                slots: [
                    {categories: ['Напитки', 'Бакалея'], include: ['чай', 'кофе']},
                    {categories: ['Хлеб и выпечка', 'Сладости'], include: ['хлеб', 'булоч', 'печен', 'шоколад']}
                ]
            },
            {
                title: 'Иммунитет',
                note: 'Мед можно упаковать в сезонный набор с чаем, ягодами или молочным SKU.',
                slots: [
                    {categories: ['Бакалея', 'Напитки'], include: ['чай', 'трав']},
                    {categories: ['Овощи и фрукты', 'Яйца и молочные продукты'], include: ['ягод', 'лимон', 'творог', 'йогурт']}
                ]
            }
        ],
        drink: [
            {
                title: 'К напитку',
                note: 'Напиток проще добавить в корзину вместе с десертом, сыром или выпечкой.',
                slots: [
                    {categories: ['Сладости', 'Хлеб и выпечка'], include: ['шоколад', 'печен', 'булоч', 'пирог']},
                    {categories: ['Сыры', 'Мед и пчеловодство'], include: ['сыр', 'мед', 'мёд']}
                ]
            },
            {
                title: 'Пикник с напитком',
                note: 'Напиток увеличивает чек, когда рядом есть готовая еда или свежий SKU.',
                slots: [
                    {categories: ['Мясо и птица', 'Сыры'], include: ['говяд', 'курин', 'сыр', 'колбас']},
                    {categories: ['Хлеб и выпечка', 'Овощи и фрукты'], include: ['хлеб', 'лаваш', 'томат', 'огур']}
                ]
            }
        ],
        pantry: [
            {
                title: 'Кухонный набор',
                note: 'Бакалею лучше привязывать к готовому сценарию: завтрак, ужин или рецепт.',
                slots: [
                    {categories: ['Овощи и фрукты', 'Сыры', 'Мясо и птица'], include: ['томат', 'зелень', 'сыр', 'говяд', 'курин']},
                    {categories: ['Хлеб и выпечка', 'Напитки'], include: ['хлеб', 'лаваш', 'чай', 'морс', 'сок']}
                ]
            },
            {
                title: 'Рецепт недели',
                note: 'Набор объясняет, куда использовать бакалейный SKU, и снижает риск случайной рекомендации.',
                slots: [
                    {categories: ['Овощи и фрукты'], include: ['томат', 'картоф', 'перец', 'зелень']},
                    {categories: ['Сыры', 'Хлеб и выпечка'], include: ['сыр', 'хлеб', 'лаваш']}
                ]
            },
            {
                title: 'Запас на неделю',
                note: 'Бакалейный SKU лучше связывать с регулярной корзиной продавца.',
                slots: [
                    {categories: ['Яйца и молочные продукты', 'Хлеб и выпечка'], include: ['молоко', 'творог', 'яйц', 'хлеб']},
                    {categories: ['Овощи и фрукты', 'Напитки'], include: ['овощ', 'фрукт', 'сок', 'морс']}
                ]
            },
            {
                title: 'Готовим дома',
                note: 'Такой набор объясняет, какие продукты продавца купить вместе для одного блюда.',
                slots: [
                    {categories: ['Мясо и птица', 'Рыба и морепродукты', 'Сыры'], include: ['говяд', 'курин', 'рыб', 'сыр']},
                    {categories: ['Овощи и фрукты'], include: ['томат', 'картоф', 'перец', 'зелень', 'лук']}
                ]
            }
        ]
    };
    return plans[kind] || plans.pantry;
}

function bestBundlePlans(event, mainItem, kind, limit = 3) {
    const plans = bundlePlans(kind);
    return plans
        .map((plan, index) => {
            const items = bundleCandidatesForPlan(event, mainItem, plan);
            return {
                plan,
                items,
                score: bundlePlanScore(event, mainItem, plan, items, index)
            };
        })
        .filter((recommendation) => recommendation.items.length > 0)
        .sort((a, b) => b.score - a.score)
        .slice(0, limit);
}

function bundleCandidatesForPlan(event, mainItem, plan) {
    const usedNames = new Set([normalizedProductName(event.product)]);
    const selected = [];
    plan.slots.forEach((slot, slotIndex) => {
        const candidate = assortmentForBundle(event, mainItem)
            .filter((item) => item.stock === undefined || item.stock === null || item.stock > 0)
            .filter((item) => normalizedProductName(item.name) !== normalizedProductName(event.product))
            .filter((item) => !usedNames.has(normalizedProductName(item.name)))
            .map((item) => ({item, score: bundleItemScore(item, slot, event, mainItem, `${event.title}:${event.product}:${slotIndex}`)}))
            .filter((entry) => entry.score > 0)
            .sort((a, b) => b.score - a.score)[0]?.item;
        if (candidate) {
            usedNames.add(normalizedProductName(candidate.name));
            selected.push(candidate);
        }
    });
    return selected;
}

function assortmentForBundle(event, mainItem) {
    const organizationId = bundleOrganizationId(event, mainItem);
    if (!organizationId) {
        return state.assortment;
    }
    return state.assortment.filter((item) => item.organizationId === organizationId);
}

function bundleItemScore(item, slot, event, mainItem, seed) {
    const value = bundleText([item.category, item.name, item.productDescription]);
    if (slot.exclude && slot.exclude.some((word) => value.includes(word))) {
        return 0;
    }
    const categoryMatches = slot.categories.includes(item.category);
    const includeMatches = (slot.include || []).filter((word) => value.includes(word)).length;
    if (!categoryMatches && includeMatches === 0) {
        return 0;
    }

    let score = categoryMatches ? 80 : 20;
    score += includeMatches * 28;
    score += Math.min(24, numberOrZero(item.monthlySales) / 3);
    score += Math.min(16, numberOrZero(item.stock) / 15);
    if (item.organizationId === bundleOrganizationId(event, mainItem)) {
        score += 80;
    }
    score -= Math.min(18, Math.max(0, (numberOrZero(item.price) - numberOrZero(mainItem?.price)) / 300));
    score += stableIndex(`${seed}:${item.name}:${item.category}`, 17) / 10;
    return score;
}

function bundlePlanScore(event, mainItem, plan, items, index) {
    const itemScore = items.reduce((sum, item) => {
        return sum + numberOrZero(item.monthlySales) * 0.25 + numberOrZero(item.stock) * 0.08;
    }, 0);
    const sameSellerCount = items.filter((item) => item.organizationId === bundleOrganizationId(event, mainItem)).length;
    const categoryDiversity = new Set(items.map((item) => item.category)).size;
    return items.length * 120
        + sameSellerCount * 80
        + categoryDiversity * 12
        + itemScore
        + stableIndex(`${event.date}:${event.product}:${plan.title}:${index}`, 19);
}

function bundleOrganizationId(event, mainItem) {
    return mainItem?.organizationId || event.organizationId || null;
}

function bundleRecommendationNote(event, recommendation) {
    const forecast = eventForecast(event);
    const addOnRevenue = recommendation.items.reduce((sum, item) => sum + numberOrZero(item.price), 0);
    const avgCheckLift = numberOrZero(event.productPrice) > 0
        ? Math.round(addOnRevenue * 100 / numberOrZero(event.productPrice))
        : 0;
    const expectedBundleOrders = Math.max(1, Math.round(forecast.expectedOrders * Math.min(0.35, 0.12 + recommendation.items.length * 0.08)));
    return `${recommendation.plan.note} Алгоритм выбрал ${recommendation.items.length} SKU этого продавца; потенциал допродажи: ${expectedBundleOrders} заказов, +${avgCheckLift}% к среднему чеку набора.`;
}

function fallbackBundle(event, kind, mainItem) {
    const titles = {
        meat: 'Мясной сет: основной SKU + повторный промокод',
        fish: 'Рыбный сет: основной SKU + повторный промокод',
        cheese: 'Сырная тарелка: основной SKU + повторный промокод',
        dairy: 'Завтрак: основной SKU + повторный промокод',
        produce: 'Свежая корзина: основной SKU + повторный промокод',
        sweet: 'Подарочный набор: основной SKU + повторный промокод',
        honey: 'Чайный набор: основной SKU + повторный промокод',
        drink: 'Набор к напитку: основной SKU + повторный промокод',
        pantry: 'Кухонный набор: основной SKU + повторный промокод'
    };
    const sellerText = mainItem?.shopName ? ` у продавца "${mainItem.shopName}"` : '';
    return {
        title: titles[kind] || titles.pantry,
        note: `Не нашли совместимые SKU${sellerText}, поэтому добавьте к товару "${event.product}" промокод на следующий заказ.`
    };
}

function bundleText(parts) {
    return parts.filter(Boolean).join(' ').toLowerCase();
}

function containsAny(value, words) {
    return words.some((word) => value.includes(word));
}

function normalizedProductName(value) {
    return String(value || '').toLowerCase().replaceAll(/[^a-zа-яё0-9]+/g, ' ').trim();
}

function stableIndex(value, size) {
    if (size <= 1) {
        return 0;
    }
    let hash = 0;
    String(value || '').split('').forEach((char) => {
        hash = (hash * 31 + char.charCodeAt(0)) | 0;
    });
    return Math.abs(hash) % size;
}

function socialPostVariant(event) {
    const code = currentPromoCode(event);
    const discount = currentDiscountPercent(event);
    const socialKey = socialTextKey(event, code, discount);
    return {
        text: state.generatedSocialTexts[socialKey] || '',
        key: socialKey,
        eventTitle: event.title,
        eventType: event.eventType,
        product: event.product,
        category: event.category,
        shopName: event.shopName || '',
        code,
        discount
    };
}

function socialTextKey(event, code = currentPromoCode(event), discount = currentDiscountPercent(event)) {
    return `${adviceKey(event, 'generated-social-text')}:${code}:${discount}`;
}

function detailsText(text, summary = 'Подробнее') {
    const value = String(text || '').trim();
    if (!value) {
        return '';
    }
    return `
        <details class="detail-text">
            <summary>${escapeHtml(summary)}</summary>
            <p>${escapeHtml(value)}</p>
        </details>
    `;
}

async function generateSocialText(button) {
    const key = button.dataset.generateSocialText;
    const textNode = button.closest('[data-social-text-surface]')?.querySelector('[data-generated-social-text]');
    const initialLabel = button.textContent.trim() || 'Сгенерировать';
    button.disabled = true;
    button.setAttribute('aria-busy', 'true');
    button.textContent = 'Генерируем...';

    try {
        const text = await requestSocialPostText(button);
        state.generatedSocialTexts[key] = text;
        saveGeneratedSocialTexts();
        if (textNode) {
            textNode.textContent = text;
            textNode.hidden = false;
        }
        button.textContent = 'Сгенерировать другой';
        showToast('Пост сгенерирован нейронкой');
    } catch (error) {
        console.warn(error);
        button.textContent = initialLabel;
        showToast(error.message || 'Не удалось сгенерировать пост');
    } finally {
        button.disabled = false;
        button.removeAttribute('aria-busy');
    }
}

async function requestSocialPostText(button) {
    try {
        const response = await sendJson(`${apiBase}/ai/social-post`, 'POST', {
            prompt: socialPostPrompt(button)
        });
        const text = normalizeAiText(response?.text);
        if (!text) {
            throw new Error('AI returned empty text');
        }
        return text;
    } catch (error) {
        return fallbackSocialPost(button);
    }
}

async function analyzeMetrics(button) {
    const textNode = button.closest('[data-metrics-analysis-surface]')?.querySelector('[data-generated-metrics-analysis]');
    const initialLabel = button.textContent.trim() || 'Проанализировать метрики';
    button.disabled = true;
    button.setAttribute('aria-busy', 'true');
    button.textContent = 'Анализируем...';

    try {
        const response = await sendJson(`${apiBase}/ai/metrics-analysis`, 'POST', {
            prompt: metricsAnalysisPrompt(button)
        });
        const text = normalizeAiText(response?.text);
        if (!text) {
            throw new Error('AI returned empty analysis');
        }
        if (textNode) {
            textNode.textContent = text;
            textNode.hidden = false;
        }
        button.textContent = 'Обновить анализ';
        showToast('Метрики проанализированы');
    } catch (error) {
        console.warn(error);
        button.textContent = initialLabel;
        showToast(error.message || 'Не удалось проанализировать метрики');
    } finally {
        button.disabled = false;
        button.removeAttribute('aria-busy');
    }
}

async function generateAiIdea(button) {
    const textNode = button.closest('[data-ai-idea-surface]')?.querySelector('[data-generated-ai-idea]');
    const initialLabel = button.textContent.trim() || 'Сгенерировать';
    button.disabled = true;
    button.setAttribute('aria-busy', 'true');
    button.textContent = 'Генерируем...';

    try {
        const text = await requestAiIdea(button);
        if (textNode) {
            textNode.textContent = text;
            textNode.hidden = false;
        }
        button.textContent = 'Сгенерировать другой вариант';
        showToast('Идея готова');
    } catch (error) {
        console.warn(error);
        if (textNode) {
            textNode.textContent = fallbackAiIdea(button);
            textNode.hidden = false;
        }
        button.textContent = initialLabel;
        showToast('Показана локальная идея');
    } finally {
        button.disabled = false;
        button.removeAttribute('aria-busy');
    }
}

async function requestAiIdea(button) {
    try {
        const response = await sendJson(`${apiBase}/ai/social-post`, 'POST', {
            prompt: aiIdeaPrompt(button)
        });
        const text = normalizeAiText(response?.text);
        return text || fallbackAiIdea(button);
    } catch (error) {
        return fallbackAiIdea(button);
    }
}

function socialPostPrompt(button) {
    const previousText = state.generatedSocialTexts[button.dataset.generateSocialText] || '';
    return `
Ты пишешь короткие посты для соцсетей фермерского маркетплейса.
Пиши по-русски, без эмодзи, без хэштегов, без markdown.
Дай 2-4 предложения до 450 символов.
Обязательно упомяни товар, повод, промокод и размер скидки.
Не выдумывай цену, сроки доставки, остатки и факты о фермере.

Событие: ${button.dataset.socialEventTitle || 'акция'}
Тип события: ${button.dataset.socialEventType || 'событие'}
Товар: ${button.dataset.socialProduct || 'фермерский товар'}
Категория: ${button.dataset.socialCategory || 'фермерские продукты'}
Продавец: ${button.dataset.socialShopName || 'фермерский продавец'}
Промокод: ${button.dataset.socialCode || 'PROMO'}
Скидка: ${numberOrZero(button.dataset.socialDiscount)}%
Прогноз заказов: ${numberOrZero(button.dataset.socialForecastOrders)}
ROI: ${numberOrZero(button.dataset.socialRoi)}%
${previousText ? `Предыдущий вариант: ${previousText}\nНе повторяй его, сделай другой заход.` : ''}
Сделай готовый пост для покупателей.
`.trim();
}

function aiIdeaPrompt(button) {
    const kind = button.dataset.aiKind || 'post';
    return `
Ты маркетолог фермерского ассортимента. Пиши по-русски, без markdown и эмодзи.
Дай готовую практичную идею для формата: ${kind}.
Если формат "channels", предложи 3 канала продвижения под целевую аудиторию и объясни почему.
Если формат "recipe", предложи рецепт или сценарий блюда без точных граммовок.
Если формат "blog", предложи тему, структуру и главный тезис.
Если формат "post", дай текст поста 2-4 предложения.

Событие: ${button.dataset.aiEventTitle}
Товар: ${button.dataset.aiProduct}
Категория: ${button.dataset.aiCategory}
Целевая аудитория: ${button.dataset.aiAudience}
Промокод: ${button.dataset.aiCode}
Скидка: ${numberOrZero(button.dataset.aiDiscount)}%
Прогноз заказов: ${numberOrZero(button.dataset.aiForecastOrders)}
ROI: ${numberOrZero(button.dataset.aiRoi)}%
`.trim();
}

function fallbackSocialPost(button) {
    return `К событию "${button.dataset.socialEventTitle || 'акция'}" предложите ${button.dataset.socialProduct || 'фермерский товар'} со скидкой ${numberOrZero(button.dataset.socialDiscount)}%. Используйте промокод ${button.dataset.socialCode || 'PROMO'} и подчеркните свежесть, понятный состав и пользу для покупателя.`;
}

function fallbackAiIdea(button) {
    const product = button.dataset.aiProduct || 'товар';
    const eventTitle = button.dataset.aiEventTitle || 'событие';
    const audience = button.dataset.aiAudience || 'покупатели категории';
    const code = button.dataset.aiCode || 'PROMO';
    const discount = numberOrZero(button.dataset.aiDiscount);
    switch (button.dataset.aiKind) {
        case 'blog':
            return `Идея для блога: "${product} к событию ${eventTitle}". Структура: почему товар подходит к поводу, как его выбрать, с чем сочетать, какой набор собрать. В конце добавить промокод ${code} на скидку ${discount}% для аудитории "${audience}".`;
        case 'recipe':
            return `Идея рецепта: сезонное блюдо с товаром "${product}". Показать простой сценарий: подготовка ингредиентов, главный шаг с товаром, подача к событию "${eventTitle}". В тексте мягко добавить промокод ${code} на ${discount}%.`;
        case 'channels':
            return `Каналы под аудиторию "${audience}": 1. Соцсети для быстрого эмоционального повода и фото товара. 2. Пуш для короткого напоминания о скидке ${discount}% и промокоде ${code}. 3. E-mail подборка для объяснения пользы товара "${product}" и добавления набора к событию "${eventTitle}".`;
        default:
            return `Пост: к событию "${eventTitle}" предложите "${product}" со скидкой ${discount}% по промокоду ${code}. Сделайте акцент на натуральности, понятном составе и том, как товар помогает быстро собрать удачную покупку под повод.`;
    }
}

function metricsAnalysisPrompt(button) {
    return `
Ты аналитик маркетинговых акций для фермерского ассортимента.
Пиши по-русски, без markdown и без эмодзи.
Дай 3 коротких вывода: стоит ли запускать акцию, главный риск, что улучшить.
Не выдумывай данных, используй только метрики ниже.

Событие: ${button.dataset.metricEventTitle}
Тип события: ${button.dataset.metricEventType}
Товар: ${button.dataset.metricProduct}
Категория: ${button.dataset.metricCategory}
Базовые заказы: ${numberOrZero(button.dataset.metricBaselineOrders)}
Сезонный спрос: ${numberOrZero(button.dataset.metricSeasonalOrders)}
Прогноз заказов: ${numberOrZero(button.dataset.metricExpectedOrders)}
Рост спроса: ${numberOrZero(button.dataset.metricUplift)}%
ROI: ${numberOrZero(button.dataset.metricRoi)}%
Остаток: ${numberOrZero(button.dataset.metricStock)} шт.
Ограничение остатком: ${button.dataset.metricStockLimited === 'true' ? 'да' : 'нет'}
Выручка: ${numberOrZero(button.dataset.metricRevenue)} ₽
Валовая прибыль: ${numberOrZero(button.dataset.metricProfit)} ₽
Затраты: ${numberOrZero(button.dataset.metricCost)} ₽
`.trim();
}

function normalizeAiText(response) {
    if (typeof response === 'string') {
        return cleanAiText(response);
    }
    const content = response?.message?.content ?? response?.content ?? response?.text ?? response?.value;
    if (Array.isArray(content)) {
        return cleanAiText(content.map((item) => {
            if (typeof item === 'string') {
                return item;
            }
            return item?.text || item?.content || '';
        }).join(' '));
    }
    if (typeof content === 'string') {
        return cleanAiText(content);
    }
    if (response && typeof response.toString === 'function' && response.toString !== Object.prototype.toString) {
        return cleanAiText(String(response));
    }
    return '';
}

function cleanAiText(text) {
    return String(text || '')
        .replaceAll('\n', ' ')
        .replaceAll(/\s{2,}/g, ' ')
        .replace(/^["«]+|["»]+$/g, '')
        .trim();
}

function eventHistory(event) {
    return (state.history || []).filter((item) => {
        const key = String(item.itemKey || '');
        return key.includes(event.title) && key.includes(event.product);
    });
}

function historyActionLabel(actionType) {
    return {
        'action-applied': 'Применено',
        'action-canceled': 'Отменено',
        'promotion-updated': 'Промо',
        'actual-updated': 'Факт'
    }[actionType] || 'Изменение';
}

function adviceKey(event, action) {
    const farmerScope = state.selectedFarmerId || 'all-farmers';
    return `${farmerScope}:${action}:${event.date}:${event.title}:${event.product}`;
}

function promoKey(event) {
    return adviceKey(event, 'promo-code');
}

function discountPercentKey(event) {
    return adviceKey(event, 'discount-percent');
}

function currentPromoCode(event) {
    return state.promoCodes[promoKey(event)] || suggestedPromoCode(event);
}

function currentDiscountPercent(event) {
    const key = discountPercentKey(event);
    if (Object.prototype.hasOwnProperty.call(state.discountPercents, key)) {
        const savedDiscount = normalizeDiscountPercent(state.discountPercents[key]);
        return savedDiscount === null ? 0 : savedDiscount;
    }

    const eventDiscount = normalizeDiscountPercent(event.discountPercent);
    if (eventDiscount !== null && eventDiscount > 0) {
        return eventDiscount;
    }

    return bestDiscountCandidate(event).discount;
}

function discountPromoControls(event) {
    const promoCodeKey = promoKey(event);
    const discountValueKey = discountPercentKey(event);
    const code = currentPromoCode(event);
    const discountPercent = currentDiscountPercent(event);
    return `
        <div class="promo-code-controls">
            <label>
                Промокод
                <input type="text"
                       value="${escapeHtml(code)}"
                       maxlength="24"
                       data-promo-code-input="${escapeHtml(promoCodeKey)}"
                       placeholder="EVENT5">
            </label>
            <label>
                Скидка, %
                <input type="number"
                       value="${escapeHtml(discountPercent)}"
                       min="0"
                       max="90"
                       step="1"
                       data-promo-discount-input="${escapeHtml(discountValueKey)}">
            </label>
        </div>
    `;
}

function suggestedPromoCode(event) {
    const promoFromOffer = event.offer.match(/промокод(?:ом)?\s+([A-Za-zА-Яа-я0-9_-]+)/i);
    if (promoFromOffer) {
        return normalizePromoCode(promoFromOffer[1]);
    }
    return normalizePromoCode(`${event.eventType || 'EVENT'}-${new Date(event.date).getDate()}`);
}

function normalizePromoCode(value) {
    return String(value)
        .trim()
        .replaceAll(/\s+/g, '-')
        .replaceAll(/[^A-Za-zА-Яа-я0-9_-]/g, '')
        .toUpperCase()
        .slice(0, 24);
}

function textWithCurrentPromoSettings(text, event) {
    const promoPattern = '[A-Za-zА-Яа-я0-9_-]+';
    const code = currentPromoCode(event);
    const discount = currentDiscountPercent(event);
    let result = String(text || '');

    result = result
        .replaceAll(new RegExp(`промокодом\\s+${promoPattern}`, 'gi'), `промокодом ${code}`)
        .replaceAll(new RegExp(`промокод\\s+${promoPattern}`, 'gi'), `промокод ${code}`)
        .replaceAll(/со скидкой\s+\d+%/gi, `со скидкой ${discount}%`)
        .replaceAll(/скидкой\s+\d+%/gi, `скидкой ${discount}%`)
        .replaceAll(/скидку\s+\d+%/gi, `скидку ${discount}%`)
        .replaceAll(/скидка\s+\d+%/gi, `скидка ${discount}%`);

    if (!/промокод/i.test(result)) {
        result = `${result} Промокод ${code}, скидка ${discount}%.`;
    }
    return result;
}

function savePromoCodeInput(input, persist = true) {
    const code = normalizePromoCode(input.value || '');
    if (!code) {
        showToast('Введите промокод');
        return false;
    }
    state.promoCodes[input.dataset.promoCodeInput] = code;
    input.value = code;
    savePromoCodes();
    if (persist) {
        persistPromotionSettingsFromControls(input.closest('.promo-code-controls'));
    }
    return true;
}

function normalizeDiscountPercent(value) {
    if (value === null || value === undefined || String(value).trim() === '') {
        return null;
    }
    const discount = Number(value);
    if (!Number.isFinite(discount) || discount < 0 || discount > 90) {
        return null;
    }
    return Math.round(discount);
}

function saveDiscountPercentInput(input, persist = true) {
    const discountPercent = normalizeDiscountPercent(input.value);
    if (discountPercent === null) {
        showToast('Укажите скидку от 0 до 90%');
        return false;
    }
    state.discountPercents[input.dataset.promoDiscountInput] = discountPercent;
    input.value = discountPercent;
    saveDiscountPercents();
    if (persist) {
        persistPromotionSettingsFromControls(input.closest('.promo-code-controls'));
    }
    return true;
}

function persistPromotionSettingsFromControls(controls) {
    const promoInput = controls?.querySelector('[data-promo-code-input]');
    const discountInput = controls?.querySelector('[data-promo-discount-input]');
    if (!promoInput || !discountInput) {
        return;
    }

    const promoCode = normalizePromoCode(promoInput.value || '');
    const discountPercent = normalizeDiscountPercent(discountInput.value);
    if (!promoCode || discountPercent === null) {
        return;
    }

    sendJson(`${apiBase}/state/promotion`, 'PUT', {
        organizationId: selectedOrganizationId(),
        promoKey: promoInput.dataset.promoCodeInput,
        discountKey: discountInput.dataset.promoDiscountInput,
        promoCode,
        discountPercent
    })
        .then((payload) => {
            applyCalendarState(payload);
            if (state.calendar) {
                renderCalendar(state.calendar);
            }
        })
        .catch((error) => console.warn('Promotion settings were not saved to DB', error));
}

function saveActuals(button) {
    const key = button.dataset.saveActual;
    const section = button.closest('.event-action-section');
    const ordersInput = [...(section?.querySelectorAll('[data-actual-orders]') || [])]
        .find((input) => input.dataset.actualOrders === key);
    const revenueInput = [...(section?.querySelectorAll('[data-actual-revenue]') || [])]
        .find((input) => input.dataset.actualRevenue === key);
    const actualOrders = Math.round(numberOrZero(ordersInput?.value));
    const actualRevenue = roundedMoney(revenueInput?.value);
    if (actualOrders < 0 || actualRevenue < 0) {
        showToast('Факт не может быть отрицательным');
        return;
    }
    state.actuals[key] = {actualOrders, actualRevenue};
    saveCampaignActuals();
    sendJson(`${apiBase}/state/actuals`, 'PUT', {
        organizationId: selectedOrganizationId(),
        actualKey: key,
        actualOrders,
        actualRevenue
    })
        .then((payload) => {
            applyCalendarState(payload);
            if (state.calendar) {
                renderCalendar(state.calendar);
            }
            showToast('Факт сохранён');
        })
        .catch((error) => {
            console.warn('Actuals were not saved to DB', error);
            if (state.calendar) {
                renderCalendar(state.calendar);
            }
            showToast('Факт сохранён локально');
        });
}

function exportCampaignPlanCsv() {
    if (!state.calendar || !state.calendar.events.length) {
        showToast('Нет событий для экспорта');
        return;
    }
    const rows = [
        ['Дата', 'Событие', 'Категория', 'Товар', 'Скидка', 'Промокод', 'Заказы план', 'ROI план', 'Остаток', 'Сегмент', 'Набор', 'Риск']
    ];
    state.calendar.events.forEach((event) => {
        const forecast = eventForecast(event);
        rows.push([
            eventPeriod(event),
            event.title,
            event.category,
            event.product,
            `${forecast.discountPercent}%`,
            currentPromoCode(event),
            forecast.expectedOrders,
            `${forecast.roiPercent}%`,
            forecast.stock,
            segmentRecommendation(event).title,
            bundleRecommendation(event).title,
            stockRisk(event).title
        ]);
    });
    const csv = rows.map((row) => row.map(csvCell).join(';')).join('\n');
    const blob = new Blob([`\uFEFF${csv}`], {type: 'text/csv;charset=utf-8'});
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `campaign-plan-${state.selectedYear}-${String(state.selectedMonth).padStart(2, '0')}.csv`;
    link.click();
    URL.revokeObjectURL(link.href);
    showToast('План экспортирован');
}

function csvCell(value) {
    return `"${String(value ?? '').replaceAll('"', '""')}"`;
}

function fillCategoryFilter(categories) {
    const select = document.getElementById('category');
    const selected = select.value;
    const mergedCategories = [...new Set([...(categories || []), ...state.assortment.map((item) => item.category)])].sort((a, b) => a.localeCompare(b, 'ru'));
    select.innerHTML = '<option value="">Все категории</option>' + mergedCategories.map((category) => `<option value="${escapeHtml(category)}">${escapeHtml(category)}</option>`).join('');
    select.value = mergedCategories.includes(selected) ? selected : '';
    renderCustomProductCategoryOptions();
}

function highlightedEventsByProduct() {
    const result = new Map();
    if (!state.calendar) {
        return result;
    }
    state.calendar.events.forEach((event) => {
        if (!result.has(event.product)) {
            result.set(event.product, event);
        }
    });
    return result;
}

function channelGroups(channels) {
    return channels.reduce((result, channel) => {
        const normalizedChannel = normalizedMarketplaceChannel(channel);
        if (marketplaceChannels.has(normalizedChannel)) {
            result.marketplace.push(normalizedChannel);
        } else {
            result.farmer.push(channel);
        }
        return result;
    }, {marketplace: [], farmer: []});
}

function channelTags(channels) {
    return channels.map((channel) => `<span>${escapeHtml(channelLabel(channel))}</span>`).join('');
}

function channelLabel(channel) {
    const action = marketplaceChannelActions[normalizedMarketplaceChannel(channel)];
    if (action) {
        return action.label;
    }
    return channel;
}

function renderChannelActionsSection(event, channels) {
    const channelActions = channelActionCards(event, channels);
    return `
        <section class="event-action-section">
            <header class="event-action-heading">
                <span class="action-icon">▦</span>
                <h3>Идеи для контента</h3>
            </header>
            ${channelActions}
        </section>
    `;
}

function channelActionCards(event, channels) {
    const uniqueChannels = [...new Set(channels.map(normalizedMarketplaceChannel))]
        .filter((channel) => channel !== 'Витрина' && marketplaceChannelActions[channel]);
    return `
        <div class="channel-action-grid">
            ${uniqueChannels.map((channel) => {
                const action = marketplaceChannelActions[channel];
                return `
                    <article class="event-action-mini channel-action-card">
                        <h4>${escapeHtml(action.label)}</h4>
                        ${detailsText(actionDescription(action, event))}
                    </article>
                `;
            }).join('')}
            ${socialPostGeneratorCard(event)}
        </div>
    `;
}

function aiContentIdeasSection(event) {
    const forecast = eventForecast(event);
    const segment = segmentRecommendation(event);
    const ideas = [
        {kind: 'post', title: 'Идея поста', button: 'Сгенерировать пост'},
        {kind: 'blog', title: 'Идея для блога', button: 'Сгенерировать блог'},
        {kind: 'recipe', title: 'Идея рецепта', button: 'Сгенерировать рецепт'},
        {kind: 'channels', title: 'Каналы под аудиторию', button: 'Подобрать каналы'}
    ];
    return `
        <section class="event-action-section">
            <header class="event-action-heading">
                <span class="action-icon">★</span>
                <h3>Генерация идей</h3>
            </header>
            <div class="channel-action-grid">
                ${ideas.map((idea) => `
                    <article class="event-action-mini channel-action-card ai-idea-card" data-ai-idea-surface>
                        <h4>${escapeHtml(idea.title)}</h4>
                        <p class="social-post-text" data-generated-ai-idea hidden></p>
                        <button class="generate-text-button social-post-generate" type="button"
                                data-generate-ai-idea
                                data-ai-kind="${escapeHtml(idea.kind)}"
                                data-ai-event-title="${escapeHtml(event.title)}"
                                data-ai-product="${escapeHtml(event.product)}"
                                data-ai-category="${escapeHtml(event.category)}"
                                data-ai-audience="${escapeHtml(segment.title)}"
                                data-ai-code="${escapeHtml(currentPromoCode(event))}"
                                data-ai-discount="${escapeHtml(currentDiscountPercent(event))}"
                                data-ai-forecast-orders="${escapeHtml(forecast.expectedOrders)}"
                                data-ai-roi="${escapeHtml(forecast.roiPercent)}">
                            ${escapeHtml(idea.button)}
                        </button>
                    </article>
                `).join('')}
            </div>
        </section>
    `;
}

function socialPostGeneratorCard(event) {
    const forecast = eventForecast(event);
    const socialPost = socialPostVariant(event);
    return `
        <article class="event-action-mini channel-action-card social-generator-card" data-social-text-surface>
            <h4>Пост для соцсетей</h4>
            ${detailsText('GigaChat соберет готовый текст по товару, событию и промокоду.')}
            <p class="social-post-text" data-generated-social-text="${escapeHtml(socialPost.key)}" hidden></p>
            <button class="generate-text-button social-post-generate" type="button"
                    data-generate-social-text="${escapeHtml(socialPost.key)}"
                    data-social-event-title="${escapeHtml(socialPost.eventTitle)}"
                    data-social-event-type="${escapeHtml(socialPost.eventType)}"
                    data-social-product="${escapeHtml(socialPost.product)}"
                    data-social-category="${escapeHtml(socialPost.category)}"
                    data-social-shop-name="${escapeHtml(socialPost.shopName)}"
                    data-social-code="${escapeHtml(socialPost.code)}"
                    data-social-discount="${escapeHtml(socialPost.discount)}"
                    data-social-forecast-orders="${escapeHtml(forecast.expectedOrders)}"
                    data-social-roi="${escapeHtml(forecast.roiPercent)}">
                Сгенерировать пост
            </button>
        </article>
    `;
}

function actionDescription(action, event) {
    return `${contentIdeaByChannel(action.label, event)} Промокод: ${currentPromoCode(event)}, скидка: ${currentDiscountPercent(event)}%.`;
}

function contentIdeaByChannel(label, event) {
    const product = event.product;
    const title = event.title;
    if (label.includes('Блог')) {
        return `Идея для блога: рассказать историю товара "${product}" через повод "${title}", добавить совет по выбору и сценарий использования.`;
    }
    if (label.includes('Рецепты')) {
        return `Идея рецепта: показать, как использовать "${product}" в сезонном блюде под событие "${title}".`;
    }
    if (label.includes('Сторисы')) {
        return `Идея сторис: короткая серия из фото товара, пользы, промокода и финального призыва сохранить подборку.`;
    }
    if (label.includes('E-mail')) {
        return `Идея рассылки: включить "${product}" в тематическую подборку и объяснить, почему он подходит к событию "${title}".`;
    }
    if (label.includes('Пуш')) {
        return `Идея пуша: коротко связать "${title}" с выгодой на "${product}" и промокодом.`;
    }
    if (label.includes('Соцсети')) {
        return `Идея поста: сделать акцент на поводе "${title}", товаре "${product}" и простой выгоде для покупателя.`;
    }
    return `Идея: связать "${product}" с событием "${title}" и предложить покупателю понятную выгоду.`;
}

function normalizedMarketplaceChannel(channel) {
    if (channel === 'Соцсети фермера' || channel === 'Соцсети и мессенджеры') {
        return 'Соцсети';
    }
    if (channel === 'E-mail') {
        return 'E-mail подборка';
    }
    if (channel === 'Пуш-уведомления') {
        return 'Пуш';
    }
    return channel;
}

async function loadAdminEvents() {
    state.adminEvents = await fetchJson(`${apiBase}/admin/events`);
    renderAdminEvents();
}

function renderAdminEvents() {
    const list = document.getElementById('admin-event-list');
    if (!state.adminEvents.length) {
        list.innerHTML = '<article class="admin-event-card empty-state">Событий пока нет.</article>';
        return;
    }

    list.innerHTML = state.adminEvents.map((event) => `
        <article class="admin-event-card">
            <div>
                <span class="badge">${escapeHtml(event.eventType)}</span>
                <h3>${escapeHtml(event.title)}</h3>
                <p>${escapeHtml(adminEventPeriod(event))} · ROI ${event.roiPercent}%</p>
                <div class="channel-tags">${channelTags(event.tags.slice(0, 6))}</div>
            </div>
            <div class="admin-card-actions">
                <button class="ghost-button" type="button" data-admin-edit="${event.id}">Редактировать</button>
                <button class="danger-button" type="button" data-admin-delete="${event.id}">Удалить</button>
            </div>
        </article>
    `).join('');
}

function renderAdminForm(event = null) {
    state.editingEventId = event ? String(event.id) : '';
    const startDate = event ? adminEventStartDate(event) : todayIsoDate();
    const endDate = event ? adminEventEndDate(event) : addDaysIsoDate(startDate, 6);
    document.getElementById('admin-event-id').value = state.editingEventId;
    document.getElementById('admin-title').value = event?.title || '';
    document.getElementById('admin-start-date').value = startDate;
    document.getElementById('admin-end-date').value = endDate;
    document.getElementById('admin-type').value = event?.eventType || 'Тренд';
    document.getElementById('admin-roi').value = event?.roiPercent || 140;
    document.getElementById('admin-tags').value = (event?.tags || []).join('\n');
    document.getElementById('admin-channels').value = (event?.channels || ['Витрина', 'Пуш', 'Соцсети']).join('\n');
    document.getElementById('admin-boost-list').innerHTML = '';
    (event?.boosts || []).forEach(addAdminBoostRow);
    if (!event) {
        addAdminBoostRow({tag: '', multiplier: 1.2, rationale: '', sourceTitle: 'Админская гипотеза', sourceUrl: ''});
    }
}

function renderAdminPresets() {
    const list = document.getElementById('admin-presets');
    list.innerHTML = deliveryPromotionPresets.map((preset, index) => `
        <button class="preset-button" type="button" data-admin-preset="${index}">
            <strong>${escapeHtml(preset.title)}</strong>
            <span>${preset.durationDays} дней · ${escapeHtml(preset.eventType)}</span>
        </button>
    `).join('');
}

function applyAdminPreset(index) {
    const preset = deliveryPromotionPresets[index];
    if (!preset) {
        return;
    }
    const startDate = document.getElementById('admin-start-date').value || todayIsoDate();
    document.getElementById('admin-title').value = preset.title;
    document.getElementById('admin-start-date').value = startDate;
    document.getElementById('admin-end-date').value = addDaysIsoDate(startDate, preset.durationDays - 1);
    document.getElementById('admin-type').value = preset.eventType;
    document.getElementById('admin-roi').value = preset.roiPercent;
    document.getElementById('admin-tags').value = preset.tags.join('\n');
    document.getElementById('admin-channels').value = preset.channels.join('\n');
    document.getElementById('admin-boost-list').innerHTML = '';
    preset.boosts.forEach(addAdminBoostRow);
}

function addAdminBoostRow(boost = {}) {
    const list = document.getElementById('admin-boost-list');
    const row = document.createElement('article');
    row.className = 'admin-boost-row';
    row.innerHTML = `
        <label>Категория или тег
            <input class="boost-tag" type="text" value="${escapeHtml(boost.tag || '')}" placeholder="Сыры">
        </label>
        <label>Множитель
            <input class="boost-multiplier" type="number" step="0.01" min="1" value="${escapeHtml(boost.multiplier || 1.2)}">
        </label>
        <label class="wide">Обоснование
            <textarea class="boost-rationale" rows="2" placeholder="Почему событие должно поднять спрос">${escapeHtml(boost.rationale || '')}</textarea>
        </label>
        <label>Источник
            <input class="boost-source-title" type="text" value="${escapeHtml(boost.sourceTitle || 'Админская гипотеза')}">
        </label>
        <label>URL источника
            <input class="boost-source-url" type="url" value="${escapeHtml(boost.sourceUrl || '')}">
        </label>
        <button class="danger-button" type="button" data-remove-boost>Удалить коэффициент</button>
    `;
    list.appendChild(row);
}

async function saveAdminEvent(event) {
    event.preventDefault();
    const eventId = document.getElementById('admin-event-id').value;
    const startDate = document.getElementById('admin-start-date').value;
    const endDate = document.getElementById('admin-end-date').value || startDate;
    const payload = {
        startDate,
        endDate,
        title: document.getElementById('admin-title').value,
        eventType: document.getElementById('admin-type').value,
        roiPercent: Number(document.getElementById('admin-roi').value),
        tags: parseAdminList(document.getElementById('admin-tags').value),
        channels: parseAdminList(document.getElementById('admin-channels').value),
        boosts: adminBoostRows()
    };

    try {
        const method = eventId ? 'PUT' : 'POST';
        const url = eventId ? `${apiBase}/admin/events/${eventId}` : `${apiBase}/admin/events`;
        const savedEvent = await sendJson(url, method, payload);
        renderAdminForm(savedEvent);
        await loadAdminEvents();
        if (state.calendarLoaded) {
            await loadCalendar();
        }
        showToast('Событие сохранено');
    } catch (error) {
        showToast('Не удалось сохранить событие');
    }
}

function editAdminEvent(eventId) {
    const event = state.adminEvents.find((item) => String(item.id) === String(eventId));
    if (!event) {
        return;
    }
    renderAdminForm(event);
    document.getElementById('admin-event-form').scrollIntoView({behavior: 'smooth', block: 'start'});
}

async function deleteAdminEvent(eventId) {
    if (!window.confirm('Удалить событие из календаря?')) {
        return;
    }
    try {
        const response = await fetch(`${apiBase}/admin/events/${eventId}`, {method: 'DELETE'});
        if (!response.ok) {
            throw new Error(`Request failed: ${response.status}`);
        }
        if (String(state.editingEventId) === String(eventId)) {
            renderAdminForm();
        }
        await loadAdminEvents();
        if (state.calendarLoaded) {
            await loadCalendar();
        }
        showToast('Событие удалено');
    } catch (error) {
        showToast('Не удалось удалить событие');
    }
}

function adminBoostRows() {
    return Array.from(document.querySelectorAll('.admin-boost-row')).map((row) => ({
        tag: row.querySelector('.boost-tag').value,
        multiplier: Number(row.querySelector('.boost-multiplier').value),
        rationale: row.querySelector('.boost-rationale').value,
        sourceTitle: row.querySelector('.boost-source-title').value,
        sourceUrl: row.querySelector('.boost-source-url').value
    })).filter((boost) => boost.tag.trim());
}

function parseAdminList(value) {
    return value.split(/[\n,]+/)
        .map((item) => item.trim())
        .filter(Boolean);
}

async function sendJson(url, method, payload) {
    const response = await fetch(url, {
        method,
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
    });
    if (!response.ok) {
        throw new Error(await responseErrorMessage(response));
    }
    return response.json();
}

async function fetchJson(url) {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(await responseErrorMessage(response));
    }
    return response.json();
}

async function responseErrorMessage(response) {
    try {
        const payload = await response.json();
        return payload.message || payload.error || `Ошибка запроса: ${response.status}`;
    } catch (error) {
        return `Ошибка запроса: ${response.status}`;
    }
}

function loadAppliedActions() {
    try {
        return JSON.parse(localStorage.getItem(appliedActionsStorageKey)) || {};
    } catch (error) {
        return {};
    }
}

function saveAppliedActions() {
    localStorage.setItem(appliedActionsStorageKey, JSON.stringify(state.appliedActions));
}

function loadPromoCodes() {
    try {
        return JSON.parse(localStorage.getItem(promoCodesStorageKey)) || {};
    } catch (error) {
        return {};
    }
}

function savePromoCodes() {
    localStorage.setItem(promoCodesStorageKey, JSON.stringify(state.promoCodes));
}

function loadDiscountPercents() {
    try {
        return JSON.parse(localStorage.getItem(discountPercentsStorageKey)) || {};
    } catch (error) {
        return {};
    }
}

function saveDiscountPercents() {
    localStorage.setItem(discountPercentsStorageKey, JSON.stringify(state.discountPercents));
}

function loadCampaignActuals() {
    try {
        return JSON.parse(localStorage.getItem(campaignActualsStorageKey)) || {};
    } catch (error) {
        return {};
    }
}

function saveCampaignActuals() {
    localStorage.setItem(campaignActualsStorageKey, JSON.stringify(state.actuals));
}

function loadCalendarHistory() {
    try {
        return JSON.parse(localStorage.getItem(calendarHistoryStorageKey)) || [];
    } catch (error) {
        return [];
    }
}

function saveCalendarHistory() {
    localStorage.setItem(calendarHistoryStorageKey, JSON.stringify(state.history || []));
}

function loadGeneratedSocialTexts() {
    try {
        return JSON.parse(localStorage.getItem(generatedSocialTextsStorageKey)) || {};
    } catch (error) {
        return {};
    }
}

function saveGeneratedSocialTexts() {
    localStorage.setItem(generatedSocialTextsStorageKey, JSON.stringify(state.generatedSocialTexts || {}));
}

function showOnboardingIfNeeded() {
    if (localStorage.getItem(onboardingStorageKey)) {
        collapseTrainingInvite();
        return;
    }
    openOnboarding();
}

function syncTrainingInviteState() {
    if (localStorage.getItem(onboardingStorageKey)) {
        collapseTrainingInvite();
    } else {
        showTrainingInvite();
    }
}

function openOnboarding() {
    state.onboardingStep = 0;
    document.getElementById('tour-layer').hidden = false;
    document.documentElement.classList.add('tour-active');
    document.body.classList.add('tour-active');
    hideTrainingControls();
    renderOnboardingStep();
}

function closeOnboarding() {
    localStorage.setItem(onboardingStorageKey, 'true');
    document.getElementById('tour-layer').hidden = true;
    document.documentElement.classList.remove('tour-active');
    document.body.classList.remove('tour-active');
    collapseTrainingInvite();
}

function showTrainingInvite() {
    document.getElementById('training-invite').hidden = false;
    document.getElementById('training-help').hidden = true;
}

function collapseTrainingInvite() {
    document.getElementById('training-invite').hidden = true;
    document.getElementById('training-help').hidden = false;
}

function hideTrainingControls() {
    document.getElementById('training-invite').hidden = true;
    document.getElementById('training-help').hidden = true;
}

function preventOnboardingScroll(event) {
    if (!document.getElementById('tour-layer').hidden) {
        event.preventDefault();
    }
}

function preventOnboardingKeyScroll(event) {
    const scrollKeys = [' ', 'Spacebar', 'PageDown', 'PageUp', 'End', 'Home', 'ArrowDown', 'ArrowUp', 'ArrowLeft', 'ArrowRight'];
    if (!document.getElementById('tour-layer').hidden && scrollKeys.includes(event.key)) {
        event.preventDefault();
        event.stopPropagation();
    }
}

function nextOnboardingStep() {
    if (state.onboardingStep >= onboardingSteps.length - 1) {
        closeOnboarding();
        return;
    }
    state.onboardingStep += 1;
    renderOnboardingStep();
}

function previousOnboardingStep() {
    state.onboardingStep = Math.max(0, state.onboardingStep - 1);
    renderOnboardingStep();
}

function renderOnboardingStep() {
    const step = onboardingSteps[state.onboardingStep];
    const examples = document.getElementById('onboarding-examples');
    document.getElementById('onboarding-icon').textContent = step.icon;
    document.getElementById('onboarding-title').textContent = step.title;
    document.getElementById('onboarding-text').textContent = step.text;
    examples.innerHTML = '';
    examples.hidden = true;
    document.getElementById('onboarding-step').textContent = `${state.onboardingStep + 1} / ${onboardingSteps.length}`;
    document.getElementById('onboarding-prev').hidden = state.onboardingStep === 0;
    document.getElementById('onboarding-next').textContent = state.onboardingStep === onboardingSteps.length - 1 ? 'Начать' : 'Далее';
    document.getElementById('onboarding-dots').innerHTML = onboardingSteps.map((_, index) =>
            `<span class="${index === state.onboardingStep ? 'active' : ''}"></span>`
    ).join('');
    focusTourTarget(step.target);
}

function focusTourTarget(selector) {
    const target = document.querySelector(selector);
    if (!target) {
        return;
    }

    target.scrollIntoView({behavior: 'smooth', block: 'center', inline: 'nearest'});
    window.scrollTo({top: window.scrollY, left: 0, behavior: 'auto'});
    window.setTimeout(updateTourPosition, 280);
}

function updateTourPosition() {
    if (document.getElementById('tour-layer').hidden) {
        return;
    }

    const step = onboardingSteps[state.onboardingStep];
    const target = document.querySelector(step.target);
    if (!target) {
        return;
    }

    const rect = target.getBoundingClientRect();
    const padding = 12;
    const spotlight = document.getElementById('tour-spotlight');
    const spotlightWidth = Math.min(window.innerWidth - 16, rect.width + padding * 2);
    const spotlightLeft = Math.max(8, Math.min(rect.left - padding, window.innerWidth - spotlightWidth - 8));
    spotlight.style.left = `${spotlightLeft}px`;
    spotlight.style.top = `${Math.max(8, rect.top - padding)}px`;
    spotlight.style.width = `${spotlightWidth}px`;
    spotlight.style.height = `${rect.height + padding * 2}px`;

    const card = document.getElementById('tour-card');
    const cardWidth = Math.min(500, window.innerWidth - 24);
    let left = rect.left;
    let top = rect.bottom + 16;
    card.style.width = `${cardWidth}px`;

    if (left + cardWidth > window.innerWidth - 12) {
        left = window.innerWidth - cardWidth - 12;
    }
    if (left < 12) {
        left = 12;
    }

    const cardHeight = card.offsetHeight || 280;
    if (top + cardHeight > window.innerHeight - 12) {
        top = Math.max(12, rect.top - cardHeight - 16);
    }

    card.style.left = `${left}px`;
    card.style.top = `${top}px`;
    card.classList.remove('animate');
    window.requestAnimationFrame(() => card.classList.add('animate'));
}

function showToast(message) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.hidden = false;
    window.clearTimeout(showToast.timeoutId);
    showToast.timeoutId = window.setTimeout(() => {
        toast.hidden = true;
    }, 2200);
}

function defaultYear() {
    return new Date().getFullYear();
}

function defaultMonthNumber() {
    return new Date().getMonth() + 1;
}

function quarterFromMonth(month) {
    return Math.ceil(month / 3);
}

function formatRub(value) {
    return new Intl.NumberFormat('ru-RU', {style: 'currency', currency: 'RUB', maximumFractionDigits: 0}).format(value);
}

function formatRubSafe(value) {
    return formatRub(numberOrZero(value));
}

function numberOrZero(value) {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : 0;
}

function signedPercent(value) {
    const percent = Math.round(numberOrZero(value));
    return `${percent > 0 ? '+' : ''}${percent}%`;
}

function signedNumber(value) {
    const number = Math.round(numberOrZero(value));
    return `${number > 0 ? '+' : ''}${number}`;
}

function formatDate(value) {
    return new Intl.DateTimeFormat('ru-RU', {day: 'numeric', month: 'long'}).format(new Date(value));
}

function formatHistoryDate(value) {
    return new Intl.DateTimeFormat('ru-RU', {day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit'}).format(new Date(value));
}

function formatDateRange(startDate, endDate) {
    if (!endDate || startDate === endDate) {
        return formatDate(startDate);
    }
    return `${formatDate(startDate)} - ${formatDate(endDate)}`;
}

function eventPeriod(event) {
    return formatDateRange(event.date, event.endDate || event.date);
}

function adminEventPeriod(event) {
    return formatDateRange(adminEventStartDate(event), adminEventEndDate(event));
}

function adminEventStartDate(event) {
    if (event.startDate) {
        return event.startDate;
    }
    return `${defaultYear()}-${String(event.eventMonth).padStart(2, '0')}-${String(event.eventDay).padStart(2, '0')}`;
}

function adminEventEndDate(event) {
    return event.endDate || adminEventStartDate(event);
}

function todayIsoDate() {
    return toIsoDate(new Date());
}

function addDaysIsoDate(value, days) {
    const date = new Date(`${value}T00:00:00`);
    date.setDate(date.getDate() + days);
    return toIsoDate(date);
}

function addDays(value, days) {
    const date = new Date(value);
    date.setDate(date.getDate() + days);
    return date;
}

function toIsoDate(date) {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
}

function monthName(value) {
    return new Intl.DateTimeFormat('ru-RU', {month: 'short'}).format(new Date(value));
}

function shortMonthName(month) {
    return new Intl.DateTimeFormat('ru-RU', {month: 'short'}).format(new Date(defaultYear(), month - 1, 1));
}

function shortText(value, max) {
    const text = String(value || '');
    return text.length > max ? `${text.slice(0, max - 1)}…` : text;
}

function initials(value) {
    const letters = String(value)
        .split(/\s+/)
        .filter(Boolean)
        .slice(0, 2)
        .map((part) => part[0])
        .join('');
    return letters || 'Ф';
}

function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}
