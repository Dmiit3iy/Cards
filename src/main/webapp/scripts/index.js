let catRes;
let index;

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
        let cookie = $.cookie('user');
        if (categoryName != null) {
            $.ajax({
                type: "POST",
                url: '/CardServer/categories',
                //Старая реализация
                // data: {"id": localStorage.userId, "category": categoryName},
                data: {"id": cookie, "category": categoryName},
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
            let foundObject = index.find(item => item.id === id);
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

    /**
     * Метод для выхода и очистки хэша
     */

    $('#btn_sign_out').click(function () {
        let cookie = $.cookie('user');
        if(cookie === undefined){
            alert('error111');
        }else {
            $.ajax({
                type: 'PUT',
                url: `/CardServer/login?id=${cookie}`,
                success: [function () {
                    $(location).attr('href', "http://localhost:8080/CardServer/login.html");
                }],
                error: [function (e) {
                    alert(JSON.stringify(e));
                }]
            });
        }
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
            index = JSON.parse(result.data);
            for (let i = 0; i < index.length; i++) {
                let markup = "<tr>" +
                    "<td>" + index[i].question + "</td>" +
                    "<td>" + index[i].answer + "</td>"
                    + `<td style="text-align: center"><a href="#" id="change_${index[i].id}"><i class="fa fa-edit" style="font-size:20px"></i></a></td>`
                    + `<td style="text-align: center"><a href="#" id="delete_${index[i].id}"><i class="fa fa-trash" style="font-size:20px"></i></a></td>`;
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
    let cookie = $.cookie('user');
    $.ajax({
        type: "get",
        url: '/CardServer/categories',
        //Старая реализация
       // data: {"id": localStorage.userId},
        data: {"id": cookie},
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
