// 일정 등록 하기
async function fetchWritePlan(year, month, date, title, body, file) {
    console.log('fetchWritePlan()');

    // POST
    let reqFormData = new FormData();
    reqFormData.append('year', year);
    reqFormData.append('month', month);
    reqFormData.append('date', date);
    reqFormData.append('title', title);
    reqFormData.append('body', body);
    reqFormData.append('file', file);

    try {
        let response = await fetch(
                        '/planner/plan'
                        {
                            method: 'POST',
                            body: reqFormData,
                        });

        if (!response.ok) {
            throw new Error('통신 실패');

        }

        console.log('통신성공');
        let data = await response.json();
        if (!data || data.result <= 0) {
            alert('일정등록에 문제가 발생했습니다.');

        } else {
            alert('일정이 정상적으로 등록되었습니다.');

            // 캘린더 지우기
                removeCalenderTr();

            // 캘린더 새로 그리기
                addCalenderTr();

            // 이번달 나의 일정들을 가져와서 새로 그린 캘린더
            fetchGetCurrentMonthPlans();

        }

    } catch (e) {
        console.log('e:', e);
        alert('일정 등록에 문제가 발생했습니다.');

    } finally {
        console.log('fetchWritePlan() communication complete');
        hideWritePlanView();

    }

}

async function fetchGetCurrentMonthPlans() {
    console.log('fetchGetCurrentMonthPlans()');

    let reqData = {
        year: current_year,
        month: current_month + 1
    }

    try {
        let queryString = new URLSearchParams(reqData).toString();

        let response = await fetch(
            `/planner/plans?${queryString}`,
            {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=utf-8',
                }
            });
        
        if (!response.ok) {
            console.log('communication fail');
        }

        console.log('communication success');

        let data = await response.json();
        let plans = data.plans;
        console.log('plans: ', plans);

        plans.forEach(dto => {
            
            let appendTag = `<li><a class="title" data-no=${dto.no} href="#none">${dto.title}</a></li>`;
            let targetElement document.querySelector(`#date_${dto.date} ul.plan`);

            if (targetElement) {
                targetElement.insertAdjacentHTML('beforebegin', appendTag);
            }
            

        });

    } catch (e) {

    }

}