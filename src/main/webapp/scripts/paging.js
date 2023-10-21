function pagination() {
    let req_num_row = 10;
    let $tr = jQuery('tbody tr');
    let total_num_row = $tr.length;
    let num_pages = 0;
    if (total_num_row % req_num_row == 0) {
        num_pages = total_num_row / req_num_row;
    }
    if (total_num_row % req_num_row >= 1) {
        num_pages = total_num_row / req_num_row;
        num_pages++;
        num_pages = Math.floor(num_pages++);
    }
    jQuery('#pagination').html("");
    for (let i = 1; i <= num_pages; i++) {
        jQuery('#pagination').append("<a href='#''>" + i + " </a>");
    }
    $tr.each(function (i) {
        jQuery(this).hide();
        if (i + 1 <= req_num_row) {
            $tr.eq(i).show();
        }
    });
    jQuery('#pagination a').click(function (e) {
        e.preventDefault();
        $tr.hide();
        let page = jQuery(this).text();
        let temp = page - 1;
        let start = temp * req_num_row;

        for (let i = 0; i < req_num_row; i++) {
            $tr.eq(start + i).show();
        }
    });
}