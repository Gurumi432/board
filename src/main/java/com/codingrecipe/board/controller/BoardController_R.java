package com.codingrecipe.board.controller;

import com.codingrecipe.board.dto.BoardDTO;
import com.codingrecipe.board.dto.CommentDTO;
import com.codingrecipe.board.service.BoardService;
import com.codingrecipe.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")//@
public class BoardController_R {
    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/save") //_
    public String showCreateForm() {
        return "save";
    }

    @PostMapping("/save") //_
    public String submitCreateForm(@ModelAttribute BoardDTO boardDTO) throws IOException {
        System.out.println("boardDTO = " + boardDTO); //_
        boardService.save(boardDTO); //_
        return "index";
    }

    @GetMapping("/") //_
    public String formList(Model model) {
        List<BoardDTO> boardDTOList = boardService.findAllboardDTO();
        model.addAttribute("boardList", boardDTOList);
        return "list";
    }

    @GetMapping("/{id}") //_
    public String viewForm(@PathVariable Long id, Model model,
                            @PageableDefault(page=1) Pageable pageable) {
/*      Pageable : Client가 Controller한테 요청하는 페이지 정보
        Model : Controller가 View한테 보내는 페이지 정보 */
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        List<CommentDTO> commentDTOList = commentService.findAll(id);
        model.addAttribute("commentList", commentDTOList);
        model.addAttribute("board", boardDTO);
        model.addAttribute("page", pageable.getPageNumber());
        return "detail"; // 다읽음
    }

    @GetMapping("/update/{id}") //@
    public String showUpdateForm(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO);
        return "update";
    }

    @PostMapping("/update")
    public String submitUpdateForm(@ModelAttribute BoardDTO boardDTO, Model model) {
        BoardDTO board = boardService.update(boardDTO);
        model.addAttribute("board", board);
        return "detail";// 다읽음
//        return "redirect:/board/" + boardDTO.getId();
    }

    @GetMapping("/delete/{id}")
    public String deleteBoard(@PathVariable Long id) {
        boardService.delete(id);
        return "redirect:/board/";
    }

    // /board/paging?page=1
    @GetMapping("/paging")
    public String listBoardsPage(@PageableDefault(page = 1) Pageable pageable, Model model) {
//        pageable.getPageNumber();
        Page<BoardDTO> boardList = boardService.paging(pageable);
        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        // page 갯수 20개
        // 현재 사용자가 3페이지
        // 1 2 3
        // 현재 사용자가 7페이지
        // 7 8 9
        // 보여지는 페이지 갯수 3개
        // 총 페이지 갯수 8개

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "paging";

    }
}
