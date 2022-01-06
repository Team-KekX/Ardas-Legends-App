
function is_long_text(text){
    return text.length >= 1900;
}

function detect_end_of_word(text, position, look_for_format){
    const slice=text.slice(position);
    if (!look_for_format){
        for (const char of slice){
            position +=1;
            if (char === ' ' || char === ',' || char === '\n'){
                return position;
            }
        }
    }
    else{
        let format_counter = 0;
        for (const char of slice){
            if (char === '`'){
                format_counter += 1;
            }
            position += 1;
            if (format_counter === 3){
                return position;
            }
        }
    }
}

function separate_long_text_local(text, look_for_format){
    if (is_long_text(text)){
        const separator = detect_end_of_word(text, 1900, look_for_format);
        const left= text.slice(0,-(text.length-separator));
        const right = text.slice(separator);
        return [left].concat(separate_long_text_local(right));
    }
    else{
        return [text];
    }
}

module.exports = {

    separate_long_text(text, look_for_format=false){
        return separate_long_text_local(text,look_for_format);
    }



};