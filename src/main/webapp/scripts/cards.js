let catRes;
let cards;

$(document).ready(function () {
    loadCategory();

    /**
     * Добавление карточки в выбранную категорию из списка
     */
    $('#add_new_card').click(function () {
        let categoryName = $('#category-select option:selected').text();
        let question1 = $('#add_new_question').val();
        let answer1 = $('#add_new_answer').val();
        if (question1 != null && answer1 != null) {
            $.ajax({
                type: "POST",
                url: `/CardServer/cards?category=${categoryName}`,
                data: {"question": question1, "answer": answer1},
                success: [function (result) {
                    $('#add_new_question').val('');
                    $('#add_new_answer').val('');
                    location.reload();
                    showTable();
                }],
                error: [function () {
                    alert("error");
                }]
            });
        } else {
            alert("Введите вопрос и ответ!!!")
        }
    });

    /**
     * Добавление категории
     */
    $('#add_new_category').click(function () {
        let categoryName = $('#add_new_name_category').val();
        if (categoryName != null) {
            $.ajax({
                type: "POST",
                url: '/CardServer/categories',
                data: {"id": localStorage.userId, "category": categoryName},
                success: [function (result) {
                    $('#add_new_name_category').val('');
                    location.reload();
                }],
                error: [function () {
                    alert("error");
                }]
            });
        } else {
            alert("Введите название категории!!!")
        }
    });

    /**
     * Удаление категории
     */
    $('#delete_category').click(function () {
        let categoryName = $('#category-select option:selected').text();
        // alert(categoryName);
        if (categoryName != null) {
            $.ajax({
                type: "DELETE",
                url: '/CardServer/categories?category=' + categoryName,
                // data: {"category": categoryName},
                success: [function (result) {
                    $('#add_new_name_category').val('');
                    location.reload();
                }],
                error: [function () {
                    alert("error");
                }]
            });
        } else {
            alert("Введите все данные!!!")
        }
    });

    /**
     * Перерисовка таблицы с карточками при событии изменения категории
     */
    $('#category-select').change(function () {
        showTable();
    })

    /**
     * Обработчик для выбора открытия модального окна для изменения карточки или принятия решения о ее удалении
     */
    $('tbody').on("click", "a", function () {
        let arr = $(this).attr('id').split('_');
        let id = Number(arr[1]);
        let value = arr[0];
        if (value === "change") {
            $('#modalChange').modal('show');
            let foundObject = cards.find(item => item.id === id);
            $('#change_answer').val(foundObject.answer);
            $('#change_question').val(foundObject.question);
            $('#change_card_id').val(foundObject.id);
        } else {
            deletePart(id);
        }
    });

    /**
     * Обработка событий для изменения карточки
     */
    $('#change-card-button').click(function () {
        let answer = $('#change_answer').val();
        let question = $('#change_question').val();
        let id = String($('#change_card_id').val());
        $.ajax({
            type: 'PUT',
            url: `/CardServer/cards?id=${id}&answer=${answer}&question=${question}`,
            contentType: 'application/json',
            success: [function (result) {
                $('#change_answer').val('');
                $('#change_question').val('');
                $('#change_part_id').val('');
                $('#modalChange').modal('hide');
                showTable();

            }],
            error: [function (e) {
                alert("error");
                alert(JSON.stringify(e));
            }]
        });
    });


});

/**
 * Метод для отрисовки таблицы карточки
 */
function showTable() {
    let categoryName1 = $('#category-select option:selected').text();
    $("tbody").html("");
    $.ajax({
        type: "GET",
        url: `/CardServer/cards?category=${categoryName1}`,
        success: [function (result) {
            cards = JSON.parse(result.data);
            for (let i = 0; i < cards.length; i++) {
                let markup = "<tr>" +
                    "<td>" + cards[i].question + "</td>" +
                    "<td>" + cards[i].answer + "</td>"
                    + `<td style="text-align: center"><a href="#" id="change_${cards[i].id}"><i class="fa fa-edit" style="font-size:20px"></i></a></td>`
                    + `<td style="text-align: center"><a href="#" id="delete_${cards[i].id}"><i class="fa fa-trash" style="font-size:20px"></i></a></td>`;
                $("table tbody").append(markup);
            }
            pagination();
        }],
        error: [function (e) {
            console.log("error");
            console.log(JSON.stringify(e));
        }]
    });
}

/**
 * Запрос для удаления карточки
 * @param id
 */
function deletePart(id) {
    $.ajax({
        type: "DELETE",
        url: `/CardServer/cards?id=${id}`,
        success: [function (result) {
            showTable();
        }],
        error: [function (e) {
            alert(JSON.stringify(e));
        }]
    });
}

/**
 * Запрос на заполнение выпадающего списка с категориями
 */
function loadCategory() {
    $.ajax({
        type: "get",
        url: '/CardServer/categories',
        data: {"id": localStorage.userId},
        success: [function (result) {
            catRes = JSON.parse(result.data);
            $('#category-select').append("<option value='' disabled selected>Выбери свою категорию</option>");
            for (let i = 0; i < catRes.length; i++) {
                $('#category-select').append("<option value=" + i + ">" + catRes[i].name + "</option>");
            }
        }],
        error: [function () {
            alert("error");
        }]
    });
}
